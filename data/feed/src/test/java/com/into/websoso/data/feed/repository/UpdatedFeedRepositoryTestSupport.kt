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
import okhttp3.MultipartBody
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
internal fun TestScope.createRepository(
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

internal fun List<FeedEntity>.feed(id: Long): FeedEntity = first { it.id == id }

internal fun feedsResponseOf(vararg feeds: FeedResponseDto): FeedsResponseDto = FeedsResponseDto(isLoadable = false, feeds = feeds.toList())

internal fun feedResponse(
    feedId: Long,
    isLiked: Boolean,
    likeCount: Int,
): FeedResponseDto =
    FeedResponseDto(
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

internal enum class LikeOutcome {
    SUCCESS,
    FAIL_WITHOUT_APPLYING,
    TIMEOUT_AFTER_APPLYING,
}

internal class PlannedLikeRequest(
    val method: String,
    val feedId: Long,
    val outcome: LikeOutcome,
    val processingGate: CompletableDeferred<Unit>?,
    val responseGate: CompletableDeferred<Unit>?,
)

internal class FakeFeedApi : FeedApi {
    var feedsResponse: FeedsResponseDto = FeedsResponseDto(isLoadable = false, feeds = emptyList())
    val feedDetailResponses = mutableMapOf<Long, FeedDetailResponseDto>()

    /** 요청 시작 순서. 서버가 요청을 받은 순서를 나타낸다. */
    val postLikesCalls = mutableListOf<Long>()
    val deleteLikesCalls = mutableListOf<Long>()

    /** 성공 응답 도착 순서. 실패 응답은 포함하지 않는다. */
    val postLikesCompleted = mutableListOf<Long>()
    val deleteLikesCompleted = mutableListOf<Long>()

    val appliedLikeRequests = mutableListOf<PlannedLikeRequest>()
    val respondedLikeRequests = mutableListOf<PlannedLikeRequest>()
    private val plannedLikeRequests = mutableListOf<PlannedLikeRequest>()

    /** 같은 method/feedId의 다음 요청부터 순서대로 적용한다. 두 gate가 없으면 즉시 처리·응답한다. */
    fun planLikeRequest(
        method: String,
        feedId: Long,
        outcome: LikeOutcome = LikeOutcome.SUCCESS,
        processingGate: CompletableDeferred<Unit>? = null,
        responseGate: CompletableDeferred<Unit>? = null,
    ): PlannedLikeRequest {
        require(method == "POST" || method == "DELETE")
        return PlannedLikeRequest(method, feedId, outcome, processingGate, responseGate).also {
            plannedLikeRequests += it
        }
    }

    override suspend fun getFeeds(
        feedsOption: String,
        lastFeedId: Long,
        size: Int,
    ): FeedsResponseDto = feedsResponse

    override suspend fun getFeed(feedId: Long): FeedDetailResponseDto = feedDetailResponses[feedId] ?: error("등록되지 않은 feedId: $feedId")

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
        executeLikeRequest("POST", feedId)
        postLikesCompleted += feedId
    }

    override suspend fun deleteLikes(feedId: Long) {
        deleteLikesCalls += feedId
        executeLikeRequest("DELETE", feedId)
        deleteLikesCompleted += feedId
    }

    private suspend fun executeLikeRequest(
        method: String,
        feedId: Long,
    ) {
        val index = plannedLikeRequests.indexOfFirst { it.method == method && it.feedId == feedId }
        val request = if (index >= 0) {
            plannedLikeRequests.removeAt(index)
        } else {
            PlannedLikeRequest(method, feedId, LikeOutcome.SUCCESS, null, null)
        }
        request.processingGate?.await()
        if (request.outcome != LikeOutcome.FAIL_WITHOUT_APPLYING) {
            applyLikeOnServer(feedId, isLiked = method == "POST")
            appliedLikeRequests += request
        }
        request.responseGate?.await()
        // 예외로 전달되는 실패/타임아웃도 앱에 결과가 도착한 순서에 포함한다.
        respondedLikeRequests += request
        when (request.outcome) {
            LikeOutcome.SUCCESS -> Unit
            LikeOutcome.FAIL_WITHOUT_APPLYING -> throw RuntimeException("$method 반영 전 실패: $feedId")
            LikeOutcome.TIMEOUT_AFTER_APPLYING -> throw SocketTimeoutException("서버 반영 후 응답 유실: $feedId")
        }
    }

    // 테스트 가정: POST/DELETE는 목표 상태를 설정하고, 같은 상태를 반복 설정해도 개수는 변하지 않는다.
    private fun applyLikeOnServer(
        feedId: Long,
        isLiked: Boolean,
    ) {
        feedDetailResponses[feedId]?.let { detail ->
            if (detail.isLiked != isLiked) {
                feedDetailResponses[feedId] = detail.copy(
                    isLiked = isLiked,
                    likeCount = detail.likeCount + if (isLiked) 1 else -1,
                )
            }
        }
        feedsResponse = feedsResponse.copy(
            feeds = feedsResponse.feeds.map { feed ->
                if (feed.feedId == feedId && feed.isLiked != isLiked) {
                    feed.copy(
                        isLiked = isLiked,
                        likeCount = feed.likeCount + if (isLiked) 1 else -1,
                    )
                } else {
                    feed
                }
            },
        )
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

internal class FakePendingFeedLikeStore(
    initial: Map<Long, Boolean> = emptyMap(),
) : PendingFeedLikeStore {
    private val state = MutableStateFlow(initial)

    override val pendingLikes: Flow<Map<Long, Boolean>> = state.asStateFlow()

    val updateCalls = mutableListOf<Pair<Long, Boolean>>()
    val deleteCalls = mutableListOf<Long>()
    val deleteIfMatchedCalls = mutableListOf<Pair<Long, Boolean>>()

    /** true면 손상된 파일을 초기화한 직후처럼 동작한다. clearResetNotice()를 호출해야 false로 바뀐다. */
    var resetNotice: Boolean = false

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

    override suspend fun readResetNotice(): Boolean = resetNotice

    override suspend fun clearResetNotice() {
        resetNotice = false
    }
}
