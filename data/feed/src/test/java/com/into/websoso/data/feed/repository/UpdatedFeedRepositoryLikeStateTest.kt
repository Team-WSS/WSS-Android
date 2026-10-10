package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.model.response.FeedDetailResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import com.into.websoso.data.feed.repository.model.LikeSyncStatus
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryLikeStateTest {
    @Test
    fun `실패 항목을 재조회해도 안내와 최신 선택이 유지된다`() =
        runTest {
            val api = server()
            api.planLikeRequest("POST", 1L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val repository = createRepository(api, FakePendingFeedLikeStore())
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            assertTrue(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
        }

    @Test
    fun `복원된 선택은 확인 필요로 표시하고 취소도 보존한다`() =
        runTest {
            val api = server()
            val store = FakePendingFeedLikeStore(mapOf(1L to true))
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            repository.retryPendingLikes(setOf(1L))
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `복원보다 먼저 발생한 클릭과 취소를 오래된 저장 기록으로 덮지 않는다`() =
        runTest {
            val store = FakePendingFeedLikeStore(mapOf(1L to true))
            val repository = createRepository(server(), store)
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertFalse(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
        }

    @Test
    fun `여러 목록에 같은 피드가 있어도 한 클릭을 한 선택으로 기록한다`() =
        runTest {
            val api = server()
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.fetchFeeds(0L, 10, "RECOMMENDED")
            repository.updateMyFeedsCache(repository.sosoAllFeeds.value, true, repository.likeStateVersion())
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertTrue(
                repository.sosoRecommendedFeeds.value
                    .single()
                    .isLiked,
            )
            assertTrue(
                repository.myFeeds.value
                    .single()
                    .isLiked,
            )
            assertEquals(listOf(1L), api.postLikesCalls)
            assertTrue(store.currentPendingLikes().isEmpty())
        }

    @Test
    fun `목록 없이 상세만 열어도 클릭과 실패 뒤 취소가 상세 상태에 반영된다`() =
        runTest {
            val api = server()
            api.feedDetailResponses[1L] = detailResponse()
            api.planLikeRequest("POST", 1L, LikeOutcome.TIMEOUT_AFTER_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeed(1L)
            repository.toggleLikeLocal(1L)
            assertTrue(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
            assertEquals(
                1,
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .likeCount,
            )
            repository.syncPendingLikes()
            advanceUntilIdle()
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertFalse(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
            assertEquals(
                0,
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .likeCount,
            )
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            repository.retryPendingLikes(setOf(1L))
            advanceUntilIdle()
            assertFalse(api.getFeed(1L).isLiked)
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `상세 조회 뒤 늦게 복원된 선택도 상세 화면이 관찰하는 상태에 반영된다`() =
        runTest {
            val api = server()
            api.feedDetailResponses[1L] = detailResponse()
            val repository = createRepository(api, FakePendingFeedLikeStore(mapOf(1L to true)))
            repository.fetchFeed(1L)
            assertFalse(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
            advanceUntilIdle()
            assertTrue(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
            assertEquals(
                1,
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .likeCount,
            )
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
        }

    @Test
    fun `전송 전 취소를 끝낸 뒤에는 새로 조회한 서버 값을 다음 선택의 기준으로 삼는다`() =
        runTest {
            val api = server()
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            api.feedsResponse = feedsResponseOf(feedResponse(1L, true, 1))
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
            assertFalse(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
        }

    @Test
    fun `다른 목록에서 갱신된 좋아요를 모든 캐시에 반영하고 그 값을 기준으로 취소한다`() =
        runTest {
            val api = server()
            api.feedDetailResponses[1L] = detailResponse()
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.updateMyFeedsCache(repository.sosoAllFeeds.value, true, repository.likeStateVersion())
            repository.fetchFeed(1L)

            api.feedsResponse = feedsResponseOf(feedResponse(1L, true, 1))
            repository.fetchFeeds(0L, 10, "RECOMMENDED")
            listOf(repository.sosoAllFeeds, repository.sosoRecommendedFeeds, repository.myFeeds).forEach { feeds ->
                assertTrue(feeds.value.single().isLiked)
                assertEquals(1, feeds.value.single().likeCount)
            }
            assertTrue(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )

            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
            assertTrue(api.postLikesCalls.isEmpty())
            assertFalse(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertTrue(store.currentPendingLikes().isEmpty())
            listOf(repository.sosoAllFeeds, repository.sosoRecommendedFeeds, repository.myFeeds).forEach { feeds ->
                assertFalse(feeds.value.single().isLiked)
                assertEquals(0, feeds.value.single().likeCount)
            }
            assertFalse(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
        }

    @Test
    fun `상세 조회 중에 좋아요를 누르고 전송이 끝나면 늦게 온 응답이 좋아요를 덮지 않는다`() =
        runTest {
            val fakeServer = server().apply { feedDetailResponses[1L] = detailResponse() }
            val response = CompletableDeferred<Unit>()
            val api = heldDetailApi(fakeServer, response)
            val repository = createRepository(api, FakePendingFeedLikeStore())
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            val detail = async { repository.fetchFeed(1L) }
            runCurrent()

            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            response.complete(Unit)
            advanceUntilIdle()

            assertTrue(detail.await().isLiked)
            assertTrue(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(1L)
                    .isLiked,
            )
        }

    @Test
    fun `좋아요를 누른 뒤 시작한 목록 조회 중에 전송이 끝나면 늦게 온 응답이 좋아요를 덮지 않는다`() =
        runTest {
            val fakeServer = server()
            val response = CompletableDeferred<Unit>()
            var holdFeeds = false
            val api = heldFeedsApi(fakeServer, response) { holdFeeds }
            val repository = createRepository(api, FakePendingFeedLikeStore())
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()

            holdFeeds = true
            val refreshed = async { repository.fetchFeeds(0L, 10, "ALL") }
            runCurrent()
            repository.syncPendingLikes()
            advanceUntilIdle()
            response.complete(Unit)
            advanceUntilIdle()

            assertTrue(
                refreshed
                    .await()
                    .feeds
                    .feed(1L)
                    .isLiked,
            )
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(1L)
                    .isLiked,
            )
        }

    @Test
    fun `전송 확인 후 저장 정리가 끝나기 전에 늦게 온 목록 응답도 좋아요를 덮지 않는다`() =
        runTest {
            val fakeServer = server()
            val response = CompletableDeferred<Unit>()
            var holdFeeds = false
            val api = heldFeedsApi(fakeServer, response) { holdFeeds }
            val backing = FakePendingFeedLikeStore()
            val cleanup = CompletableDeferred<Unit>()
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun deletePendingLike(feedId: Long) {
                    cleanup.await()
                    backing.deletePendingLike(feedId)
                }
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()

            holdFeeds = true
            val refreshed = async { repository.fetchFeeds(0L, 10, "ALL") }
            runCurrent()
            repository.syncPendingLikes()
            advanceUntilIdle()
            // POST는 끝났고 pending도 정리됐지만, 저장 정리(삭제)는 아직 멈춰 있는 상태
            assertEquals(listOf(1L), fakeServer.postLikesCompleted)
            response.complete(Unit)
            advanceUntilIdle()

            assertTrue(
                refreshed
                    .await()
                    .feeds
                    .feed(1L)
                    .isLiked,
            )
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(1L)
                    .isLiked,
            )
            cleanup.complete(Unit)
            advanceUntilIdle()
            assertTrue(backing.currentPendingLikes().isEmpty())
        }

    @Test
    fun `내 피드 조회를 시작한 뒤 좋아요가 바뀌면 오래된 내 피드 데이터가 좋아요를 덮지 않는다`() =
        runTest {
            val repository = createRepository(server(), FakePendingFeedLikeStore())
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            val staleFeeds = repository.sosoAllFeeds.value
            val version = repository.likeStateVersion()

            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            repository.updateMyFeedsCache(staleFeeds, isRefreshed = true, likeStateVersion = version)

            assertTrue(
                repository.myFeeds.value
                    .feed(1L)
                    .isLiked,
            )
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(1L)
                    .isLiked,
            )
        }

    @Test
    fun `화면에 없는 피드도 복원한 좋아요가 목록 조회 중에 전송되면 늦게 온 응답이 덮지 않는다`() =
        runTest {
            val fakeServer = server()
            val response = CompletableDeferred<Unit>()
            val api = heldFeedsApi(fakeServer, response) { true }
            val repository = createRepository(api, FakePendingFeedLikeStore(mapOf(1L to true)))
            advanceUntilIdle()

            val firstPage = async { repository.fetchFeeds(0L, 10, "ALL") }
            runCurrent()
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), fakeServer.postLikesCompleted)
            response.complete(Unit)
            advanceUntilIdle()

            val feed = firstPage
                .await()
                .feeds
                .feed(1L)
            assertTrue(feed.isLiked)
            assertEquals(1, feed.likeCount)
        }

    @Test
    fun `화면에 없는 피드도 복원한 좋아요가 상세 조회 중에 전송되면 늦게 온 응답이 덮지 않는다`() =
        runTest {
            val fakeServer = server().apply { feedDetailResponses[1L] = detailResponse() }
            val response = CompletableDeferred<Unit>()
            val api = heldDetailApi(fakeServer, response)
            val repository = createRepository(api, FakePendingFeedLikeStore(mapOf(1L to true)))
            advanceUntilIdle()

            val detail = async { repository.fetchFeed(1L) }
            runCurrent()
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), fakeServer.postLikesCompleted)
            response.complete(Unit)
            advanceUntilIdle()

            val result = detail.await()
            assertTrue(result.isLiked)
            assertEquals(1, result.likeCount)
            assertTrue(
                repository.feedDetailLikeStates.value
                    .getValue(1L)
                    .isLiked,
            )
        }

    // 조회를 시작한 순간의 서버 값으로 응답을 만들어 두고, 도착만 늦춘다.
    private fun heldDetailApi(
        fakeServer: FakeFeedApi,
        response: CompletableDeferred<Unit>,
    ): FeedApi =
        object : FeedApi by fakeServer {
            override suspend fun getFeed(feedId: Long): FeedDetailResponseDto {
                val staleDetail = fakeServer.getFeed(feedId)
                response.await()
                return staleDetail
            }
        }

    // 조회를 시작한 순간의 서버 값으로 응답을 만들어 두고, 도착만 늦춘다.
    private fun heldFeedsApi(
        fakeServer: FakeFeedApi,
        response: CompletableDeferred<Unit>,
        shouldHold: () -> Boolean,
    ): FeedApi =
        object : FeedApi by fakeServer {
            override suspend fun getFeeds(
                feedsOption: String,
                lastFeedId: Long,
                size: Int,
            ): FeedsResponseDto {
                val staleFeeds = fakeServer.getFeeds(feedsOption, lastFeedId, size)
                if (shouldHold()) response.await()
                return staleFeeds
            }
        }

    private fun detailResponse() =
        FeedDetailResponseDto(
            userId = 1L,
            feedId = 1L,
            nickname = "nickname",
            avatarImage = "avatar",
            createdDate = "2026-01-01",
            feedContent = "feed",
            likeCount = 0,
            isLiked = false,
            commentCount = 0,
            novelId = null,
            title = null,
            novelRating = null,
            novelRatingCount = null,
            isSpoiler = false,
            isModified = false,
            isMyFeed = false,
            isPublic = true,
            images = emptyList(),
            novelThumbnailImage = null,
            novelGenre = null,
            novelAuthor = null,
            feedWriterNovelRating = null,
            novelDescription = null,
        )

    private fun server() =
        FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(1L, false, 0))
        }
}
