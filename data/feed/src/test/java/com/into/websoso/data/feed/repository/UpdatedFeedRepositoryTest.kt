package com.into.websoso.data.feed.repository

import android.content.ContextWrapper
import com.into.websoso.core.common.image.ImageCompressor
import com.into.websoso.core.network.common.ImageDownloader
import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.mapper.MultiPartMapper
import com.into.websoso.core.network.datasource.feed.model.request.CommentRequestDto
import com.into.websoso.core.network.datasource.feed.model.response.CommentsResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedDetailResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryTest {

    @Test
    fun `좋아요를 누르고 동기화하면 서버 요청이 성공하고 대기 기록이 정리된다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(1L)
        advanceUntilIdle()

        val toggled = repository.sosoAllFeeds.value.feed(1L)
        assertTrue(toggled.isLiked)
        assertEquals(4, toggled.likeCount)
        assertEquals(mapOf(1L to true), pendingStore.currentPendingLikes())

        repository.syncPendingLikes()
        advanceUntilIdle()

        assertEquals(listOf(1L), feedApi.postLikesCalls)
        assertTrue(feedApi.deleteLikesCalls.isEmpty())
        assertTrue(pendingStore.currentPendingLikes().isEmpty())

        val synced = repository.sosoAllFeeds.value.feed(1L)
        assertTrue(synced.isLiked)
        assertEquals(4, synced.likeCount)

        // 메모리 pending이 비었는지 간접 확인: 다시 동기화해도 추가 요청이 없어야 한다.
        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(1L), feedApi.postLikesCalls)
    }

    @Test
    fun `좋아요를 취소하고 동기화하면 서버 요청이 성공하고 대기 기록이 정리된다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 11L, isLiked = true, likeCount = 4))
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(11L)
        advanceUntilIdle()

        val toggled = repository.sosoAllFeeds.value.feed(11L)
        assertFalse(toggled.isLiked)
        assertEquals(3, toggled.likeCount)
        assertEquals(mapOf(11L to false), pendingStore.currentPendingLikes())

        repository.syncPendingLikes()
        advanceUntilIdle()

        assertEquals(listOf(11L), feedApi.deleteLikesCalls)
        assertTrue(feedApi.postLikesCalls.isEmpty())
        assertTrue(pendingStore.currentPendingLikes().isEmpty())
    }

    @Test
    fun `전송 전에 좋아요를 다시 취소하면 서버 요청 없이 원래 상태로 남는다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 2L, isLiked = false, likeCount = 5))
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")

        repository.toggleLikeLocal(2L)
        advanceUntilIdle()
        assertEquals(mapOf(2L to true), pendingStore.currentPendingLikes())

        repository.toggleLikeLocal(2L)
        advanceUntilIdle()

        val reverted = repository.sosoAllFeeds.value.feed(2L)
        assertFalse(reverted.isLiked)
        assertEquals(5, reverted.likeCount)
        assertTrue(pendingStore.currentPendingLikes().isEmpty())

        repository.syncPendingLikes()
        advanceUntilIdle()

        assertTrue(feedApi.postLikesCalls.isEmpty())
        assertTrue(feedApi.deleteLikesCalls.isEmpty())
    }

    @Test
    fun `동기화 요청이 실패하면 대기 기록이 메모리와 저장소에 그대로 남는다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 3L, isLiked = false, likeCount = 2))
            failingLikeIds = setOf(3L)
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(3L)
        advanceUntilIdle()

        repository.syncPendingLikes()
        advanceUntilIdle()

        assertEquals(listOf(3L), feedApi.postLikesCalls)
        assertTrue(pendingStore.deleteIfMatchedCalls.isEmpty())
        assertEquals(mapOf(3L to true), pendingStore.currentPendingLikes())

        val afterFailure = repository.sosoAllFeeds.value.feed(3L)
        assertTrue(afterFailure.isLiked)
        assertEquals(3, afterFailure.likeCount)

        // 메모리에도 남아 있는지 간접 확인: 실패 원인을 없애고 다시 동기화하면 같은 요청이 다시 나가고, 이번엔 정리된다.
        feedApi.failingLikeIds = emptySet()
        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(3L, 3L), feedApi.postLikesCalls)
        assertTrue(pendingStore.currentPendingLikes().isEmpty())
    }

    @Test
    fun `두 건을 동기화할 때 하나만 실패하면 성공한 건만 정리되고 실패한 건만 남는다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(
                feedResponse(feedId = 4L, isLiked = false, likeCount = 1),
                feedResponse(feedId = 5L, isLiked = false, likeCount = 1),
            )
            failingLikeIds = setOf(5L)
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(4L)
        repository.toggleLikeLocal(5L)
        advanceUntilIdle()
        assertEquals(mapOf(4L to true, 5L to true), pendingStore.currentPendingLikes())

        repository.syncPendingLikes()
        advanceUntilIdle()

        assertEquals(listOf(4L, 5L), feedApi.postLikesCalls)
        assertEquals(mapOf(5L to true), pendingStore.currentPendingLikes())

        val feed4 = repository.sosoAllFeeds.value.feed(4L)
        val feed5 = repository.sosoAllFeeds.value.feed(5L)
        assertTrue(feed4.isLiked)
        assertEquals(2, feed4.likeCount)
        assertTrue(feed5.isLiked)
        assertEquals(2, feed5.likeCount)

        // 메모리 확인: 다시 동기화하면 실패했던 5만 다시 시도된다.
        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(4L, 5L, 5L), feedApi.postLikesCalls)
    }

    @Test
    fun `전송 중에 취소하면 화면의 취소가 유지되고 취소가 다시 대기열에 쌓인다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 6L, isLiked = false, likeCount = 0))
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(6L)
        advanceUntilIdle()

        val postGate = feedApi.holdPostLikes(6L)
        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(6L), feedApi.postLikesCalls)

        repository.toggleLikeLocal(6L) // 좋아요 응답을 받기 전에 취소
        advanceUntilIdle()

        val cancelled = repository.sosoAllFeeds.value.feed(6L)
        assertFalse(cancelled.isLiked)
        assertEquals(0, cancelled.likeCount)

        postGate.complete(Unit) // 뒤늦게 좋아요 요청이 성공 응답을 받음
        advanceUntilIdle()

        val afterPostSucceeds = repository.sosoAllFeeds.value.feed(6L)
        assertFalse(afterPostSucceeds.isLiked) // 화면의 취소가 유지된다
        assertEquals(0, afterPostSucceeds.likeCount)
        assertEquals(mapOf(6L to false), pendingStore.currentPendingLikes()) // 취소가 다시 대기열에 남는다

        repository.syncPendingLikes() // 명시적으로 다시 동기화
        advanceUntilIdle()

        assertEquals(listOf(6L), feedApi.deleteLikesCalls)
        assertTrue(pendingStore.currentPendingLikes().isEmpty())

        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(6L), feedApi.deleteLikesCalls) // 메모리도 비어 추가 요청이 없다
    }

    @Test
    fun `동기화 호출이 겹쳐서 요청이 중복돼도 최신 선택은 손실 없이 반영된다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 7L, isLiked = false, likeCount = 0))
        }
        val pendingStore = FakePendingFeedLikeStore()
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle()

        repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
        repository.toggleLikeLocal(7L)
        advanceUntilIdle()

        val firstPostGate = feedApi.holdPostLikes(7L)
        repository.syncPendingLikes() // 첫 번째 동기화: 요청은 서버로 보냈지만 응답은 보류된다
        advanceUntilIdle()
        assertEquals(listOf(7L), feedApi.postLikesCalls) // 서버가 받은 순서 1번째

        repository.syncPendingLikes() // 두 번째 동기화(겹침): pending이 아직 남아 있어 다시 요청한다
        advanceUntilIdle()
        // 두 번째 요청은 보류 없이 즉시 응답한다 -> 응답은 두 번째 요청이 먼저 도착한다
        assertEquals(listOf(7L, 7L), feedApi.postLikesCalls) // 서버가 받은 순서: 1번, 2번
        assertEquals(listOf(7L), feedApi.postLikesCompleted) // 앱이 응답을 받은 순서: 2번이 먼저 끝남
        assertTrue(pendingStore.currentPendingLikes().isEmpty()) // 두 번째 요청 처리로 pending이 정리됨

        firstPostGate.complete(Unit) // 첫 번째 요청의 응답이 뒤늦게 도착
        advanceUntilIdle()
        assertEquals(listOf(7L, 7L), feedApi.postLikesCompleted)

        val finalFeed = repository.sosoAllFeeds.value.feed(7L)
        assertTrue(finalFeed.isLiked) // 화면의 마지막 선택: 손실 없이 좋아요 상태 유지
        assertEquals(1, finalFeed.likeCount)
        assertTrue(pendingStore.currentPendingLikes().isEmpty()) // 뒤늦은 응답이 지울 대상이 없어도 안전하게 무시된다

        repository.syncPendingLikes()
        advanceUntilIdle()
        assertEquals(listOf(7L, 7L), feedApi.postLikesCalls) // 메모리도 비어 추가 요청이 없다
    }

    @Test
    fun `복원이 끝난 뒤 목록을 조회하면 복원된 pending이 즉시 반영된다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
        }
        val pendingStore = FakePendingFeedLikeStore(initial = mapOf(1L to true))
        val repository = createRepository(feedApi, pendingStore)
        advanceUntilIdle() // init { restorePendingLikes() } 완료를 기다린다

        val result = repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")

        val restoredFeed = result.feeds.feed(1L)
        assertTrue(restoredFeed.isLiked)
        assertEquals(4, restoredFeed.likeCount)

        val cachedFeed = repository.sosoAllFeeds.value.feed(1L)
        assertTrue(cachedFeed.isLiked)
        assertEquals(4, cachedFeed.likeCount)

        // 복원된 기록이 이후 명시적 동기화에서 실제로 쓰이는지 확인한다.
        repository.syncPendingLikes()
        advanceUntilIdle()

        assertEquals(listOf(1L), feedApi.postLikesCalls)
        assertTrue(pendingStore.currentPendingLikes().isEmpty())
    }

    @Test
    fun `복원이 끝나기 전에 목록을 조회해도 복원이 끝나면 목록에 뒤늦게 반영된다`() = runTest {
        val feedApi = FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
        }
        val pendingStore = FakePendingFeedLikeStore(initial = mapOf(1L to true))
        val repository = createRepository(feedApi, pendingStore)

        // advanceUntilIdle()을 호출하지 않아 init { restorePendingLikes() }가 아직 실행되지 않은 상태에서 조회한다.
        val result = repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")

        val beforeRestore = result.feeds.feed(1L)
        assertFalse(beforeRestore.isLiked)
        assertEquals(3, beforeRestore.likeCount)

        advanceUntilIdle() // restorePendingLikes()가 뒤늦게 끝난다

        val afterRestore = repository.sosoAllFeeds.value.feed(1L)
        assertTrue(afterRestore.isLiked)
        assertEquals(4, afterRestore.likeCount)
    }

    private fun TestScope.createRepository(
        feedApi: FeedApi,
        pendingFeedLikeStore: PendingFeedLikeStore,
    ): UpdatedFeedRepository =
        UpdatedFeedRepository(
            feedApi = feedApi,
            pendingFeedLikeStore = pendingFeedLikeStore,
            multiPartMapper = MultiPartMapper(ContextWrapper(null)),
            imageDownloader = ImageDownloader(ContextWrapper(null)),
            imageCompressor = ImageCompressor(ContextWrapper(null)),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    private fun List<FeedEntity>.feed(id: Long): FeedEntity = first { it.id == id }

    private fun feedsResponseOf(vararg feeds: FeedResponseDto): FeedsResponseDto =
        FeedsResponseDto(isLoadable = false, feeds = feeds.toList())

    private fun feedResponse(
        feedId: Long,
        isLiked: Boolean,
        likeCount: Int,
    ): FeedResponseDto = FeedResponseDto(
        userId = 1L,
        nickname = "닉네임",
        avatarImage = "avatar.png",
        feedId = feedId,
        createdDate = "2026-01-01",
        feedContent = "content-$feedId",
        likeCount = likeCount,
        isLiked = isLiked,
        commentCount = 0,
        novelId = 100L,
        title = "novel-title",
        novelRating = 4.5f,
        novelRatingCount = 10,
        isSpoiler = false,
        isModified = false,
        isMyFeed = false,
        isPublic = true,
        thumbnailUrl = null,
        imageCount = 0,
        genreName = "로맨스",
        userNovelRating = null,
        feedWriterNovelRating = null,
    )
}

private class FakeFeedApi : FeedApi {
    var feedsResponse: FeedsResponseDto = FeedsResponseDto(isLoadable = false, feeds = emptyList())
    val feedDetailResponses = mutableMapOf<Long, FeedDetailResponseDto>()

    /** 요청 시작 순서. 서버가 요청을 받은 순서를 나타낸다. */
    val postLikesCalls = mutableListOf<Long>()
    val deleteLikesCalls = mutableListOf<Long>()

    /** 응답 도착 순서. 앱이 응답을 받은 순서를 나타낸다. */
    val postLikesCompleted = mutableListOf<Long>()
    val deleteLikesCompleted = mutableListOf<Long>()

    var failingLikeIds: Set<Long> = emptySet()

    private data class Gate(val method: String, val feedId: Long)

    private val gates = mutableMapOf<Gate, CompletableDeferred<Unit>>()

    /** 다음 postLikes(feedId) 호출을 [CompletableDeferred]가 완료될 때까지 보류시킨다. */
    fun holdPostLikes(feedId: Long): CompletableDeferred<Unit> = hold("POST", feedId)

    /** 다음 deleteLikes(feedId) 호출을 [CompletableDeferred]가 완료될 때까지 보류시킨다. */
    fun holdDeleteLikes(feedId: Long): CompletableDeferred<Unit> = hold("DELETE", feedId)

    private fun hold(
        method: String,
        feedId: Long,
    ): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        gates[Gate(method, feedId)] = deferred
        return deferred
    }

    override suspend fun getFeeds(
        feedsOption: String,
        lastFeedId: Long,
        size: Int,
    ): FeedsResponseDto = feedsResponse

    override suspend fun getFeed(feedId: Long): FeedDetailResponseDto =
        feedDetailResponses[feedId] ?: error("등록되지 않은 feedId: $feedId")

    override suspend fun postFeed(
        feedRequestDto: MultipartBody.Part,
        images: List<MultipartBody.Part>?,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun putFeed(
        feedId: Long,
        feedRequestDto: MultipartBody.Part,
        images: List<MultipartBody.Part>?,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun deleteFeed(feedId: Long): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun postLikes(feedId: Long) {
        postLikesCalls += feedId
        gates.remove(Gate("POST", feedId))?.await()
        postLikesCompleted += feedId
        if (feedId in failingLikeIds) throw RuntimeException("postLikes 실패: $feedId")
    }

    override suspend fun deleteLikes(feedId: Long) {
        deleteLikesCalls += feedId
        gates.remove(Gate("DELETE", feedId))?.await()
        deleteLikesCompleted += feedId
        if (feedId in failingLikeIds) throw RuntimeException("deleteLikes 실패: $feedId")
    }

    override suspend fun postSpoilerFeed(feedId: Long): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun postImpertinenceFeed(feedId: Long): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun getComments(feedId: Long): CommentsResponseDto = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun postComment(
        feedId: Long,
        commentRequestDto: CommentRequestDto,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun putComment(
        feedId: Long,
        commentId: Long,
        commentRequestDto: CommentRequestDto,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun deleteComment(
        feedId: Long,
        commentId: Long,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun postSpoilerComment(
        feedId: Long,
        commentId: Long,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")

    override suspend fun postImpertinenceComment(
        feedId: Long,
        commentId: Long,
    ): Unit = error("좋아요 테스트에서 사용하지 않음")
}

private class FakePendingFeedLikeStore(
    initial: Map<Long, Boolean> = emptyMap(),
) : PendingFeedLikeStore {
    private val state = MutableStateFlow(initial)

    override val pendingLikes: Flow<Map<Long, Boolean>> = state.asStateFlow()

    val updateCalls = mutableListOf<Pair<Long, Boolean>>()
    val deleteCalls = mutableListOf<Long>()
    val deleteIfMatchedCalls = mutableListOf<Pair<Long, Boolean>>()

    fun currentPendingLikes(): Map<Long, Boolean> = state.value

    override suspend fun getPendingLikes(): Map<Long, Boolean> = state.value

    override suspend fun updatePendingLike(
        feedId: Long,
        isLiked: Boolean,
    ) {
        updateCalls += feedId to isLiked
        state.update { it + (feedId to isLiked) }
    }

    override suspend fun deletePendingLike(feedId: Long) {
        deleteCalls += feedId
        state.update { it - feedId }
    }

    override suspend fun deletePendingLikeIfMatched(
        feedId: Long,
        isLiked: Boolean,
    ): Boolean {
        deleteIfMatchedCalls += feedId to isLiked
        if (state.value[feedId] != isLiked) return false
        state.update { it - feedId }
        return true
    }
}
