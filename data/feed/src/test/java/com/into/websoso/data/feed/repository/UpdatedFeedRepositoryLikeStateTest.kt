package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.model.response.FeedDetailResponseDto
import com.into.websoso.data.feed.repository.model.LikeSyncStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
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
            repository.updateMyFeedsCache(repository.sosoAllFeeds.value, true)
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
            repository.updateMyFeedsCache(repository.sosoAllFeeds.value, true)
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
