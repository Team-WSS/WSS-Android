package com.into.websoso.data.feed.repository

import com.into.websoso.data.feed.model.FeedEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryTest {
    @Test
    fun `좋아요를 누르고 동기화하면 서버 요청이 성공하고 대기 기록이 정리된다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
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
            assertServerMatches(feedApi, synced)

            // 메모리 pending이 비었는지 간접 확인: 다시 동기화해도 추가 요청이 없어야 한다.
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), feedApi.postLikesCalls)
        }

    @Test
    fun `좋아요를 취소하고 동기화하면 서버 요청이 성공하고 대기 기록이 정리된다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 11L, isLiked = true, likeCount = 4))
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
            assertServerMatches(feedApi, repository.sosoAllFeeds.value.feed(11L))
        }

    @Test
    fun `전송 전에 좋아요를 다시 취소하면 서버 요청 없이 원래 상태로 남는다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 2L, isLiked = false, likeCount = 5))
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
            assertServerMatches(feedApi, reverted)
        }

    @Test
    fun `동기화 요청이 실패하면 대기 기록이 메모리와 저장소에 그대로 남는다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 3L, isLiked = false, likeCount = 2))
                planLikeRequest("POST", 3L, outcome = LikeOutcome.FAIL_WITHOUT_APPLYING)
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

            // 서버 원본은 false다. 목록 병합은 메모리 pending만 보므로 재조회 결과로 메모리 보존을 확인한다.
            assertFalse(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertTrue(feedApi.appliedLikeRequests.isEmpty())
            val refreshed =
                repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(3L)
            assertTrue(refreshed.isLiked)
            assertEquals(3, refreshed.likeCount)
            assertEquals(mapOf(3L to true), pendingStore.currentPendingLikes())

            // 다음 요청은 성공하도록 두고 명시적 재동기화를 확인한다.
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(3L, 3L), feedApi.postLikesCalls)
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            assertServerMatches(feedApi, repository.sosoAllFeeds.value.feed(3L))
        }

    @Test
    fun `현재 결함 기록 - 서버 반영 후 타임아웃이 나면 이후 취소 기록이 사라지고 재조회 시 좋아요가 켜진다`() =
        runTest {
            // 승인된 재현 예상값을 기록한다. 결함을 허용하는 제품 요구사항이나 수정 완료 테스트가 아니다.
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 8L, isLiked = false, likeCount = 0))
            }
            val post = feedApi.planLikeRequest("POST", 8L, outcome = LikeOutcome.TIMEOUT_AFTER_APPLYING)
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(8L)
            advanceUntilIdle()
            repository.syncPendingLikes()
            advanceUntilIdle()

            // 서버는 반영했지만 앱은 성공 응답을 받지 못한 상태다.
            assertEquals(listOf(8L), feedApi.postLikesCalls)
            assertTrue(feedApi.postLikesCompleted.isEmpty())
            assertEquals(listOf(post), feedApi.appliedLikeRequests)
            assertEquals(listOf(post), feedApi.respondedLikeRequests)
            assertTrue(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(8L)
                    .isLiked,
            )
            assertEquals(mapOf(8L to true), pendingStore.currentPendingLikes())

            repository.toggleLikeLocal(8L)
            advanceUntilIdle()
            val cancelled = repository.sosoAllFeeds.value.feed(8L)
            assertFalse(cancelled.isLiked)
            assertEquals(0, cancelled.likeCount)
            assertTrue(pendingStore.currentPendingLikes().isEmpty())

            // 저장소가 비었고 재동기화에서도 요청이 없으므로 메모리에도 취소 pending이 남지 않았다.
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(8L), feedApi.postLikesCalls)
            assertTrue(feedApi.deleteLikesCalls.isEmpty())
            assertFalse(
                repository.sosoAllFeeds.value
                    .feed(8L)
                    .isLiked,
            )
            val serverAfterSync = feedApi.feedsResponse.feeds.single()
            assertTrue(serverAfterSync.isLiked)
            assertEquals(1, serverAfterSync.likeCount)

            val refreshed =
                repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(8L)
            assertTrue(refreshed.isLiked)
            assertEquals(1, refreshed.likeCount)
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(8L)
                    .isLiked,
            )
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `현재 결함 기록 - 중복 POST와 취소가 겹치면 서버 처리 순서와 다른 응답 순서가 취소 기록을 지운다`() =
        runTest {
            // 재현 예상: 서버 B-C-A, 앱 응답 B-A-C이면 서버 true / 화면 false / pending 없음이 된다.
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 9L, isLiked = false, likeCount = 0))
            }
            val processA = CompletableDeferred<Unit>()
            val respondC = CompletableDeferred<Unit>()
            val postA = feedApi.planLikeRequest("POST", 9L, processingGate = processA)
            val postB = feedApi.planLikeRequest("POST", 9L)
            val deleteC = feedApi.planLikeRequest("DELETE", 9L, responseGate = respondC)
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(9L)
            advanceUntilIdle()
            repository.syncPendingLikes() // A: 서버 처리부터 보류한다.
            advanceUntilIdle()
            assertEquals(listOf(9L), feedApi.postLikesCalls)
            assertTrue(feedApi.appliedLikeRequests.isEmpty())

            repository.syncPendingLikes() // B: 먼저 서버 반영 및 성공 응답을 받는다.
            advanceUntilIdle()
            assertEquals(listOf(9L, 9L), feedApi.postLikesCalls)
            assertEquals(listOf(postB), feedApi.appliedLikeRequests)
            assertEquals(listOf(postB), feedApi.respondedLikeRequests)
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .isLiked,
            )
            assertTrue(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )

            repository.toggleLikeLocal(9L)
            advanceUntilIdle()
            repository.syncPendingLikes() // C: 서버에는 취소를 반영하지만 응답은 보류한다.
            advanceUntilIdle()
            assertEquals(listOf(9L), feedApi.deleteLikesCalls)
            assertEquals(listOf(postB, deleteC), feedApi.appliedLikeRequests)
            assertEquals(listOf(postB), feedApi.respondedLikeRequests)
            assertFalse(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertFalse(
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .isLiked,
            )
            assertEquals(mapOf(9L to false), pendingStore.currentPendingLikes())

            processA.complete(Unit) // A: C보다 늦게 서버 반영하고, C보다 먼저 앱에 응답한다.
            advanceUntilIdle()
            assertEquals(listOf(postB, deleteC, postA), feedApi.appliedLikeRequests)
            assertEquals(listOf(postB, postA), feedApi.respondedLikeRequests)
            assertTrue(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertFalse(
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .isLiked,
            )
            assertEquals(mapOf(9L to false), pendingStore.currentPendingLikes())

            // 서버 true가 메모리 pending false로 덮이는지, 저장소 재전송과 별개로 확인한다.
            val beforeC =
                repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(9L)
            assertFalse(beforeC.isLiked)
            assertEquals(0, beforeC.likeCount)

            respondC.complete(Unit)
            advanceUntilIdle()
            assertEquals(listOf(postB, postA, deleteC), feedApi.respondedLikeRequests)
            assertFalse(
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .isLiked,
            )
            assertEquals(
                0,
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .likeCount,
            )
            assertTrue(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertEquals(
                1,
                feedApi.feedsResponse.feeds
                    .single()
                    .likeCount,
            )
            assertTrue(pendingStore.currentPendingLikes().isEmpty())

            // 저장소가 비어 있고 재동기화에도 추가 요청이 없어 메모리 pending도 없음을 확인한다.
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(9L, 9L), feedApi.postLikesCalls)
            assertEquals(listOf(9L), feedApi.deleteLikesCalls)
            assertFalse(
                repository.sosoAllFeeds.value
                    .feed(9L)
                    .isLiked,
            )
            assertTrue(
                feedApi.feedsResponse.feeds
                    .single()
                    .isLiked,
            )

            val refreshed =
                repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(9L)
            assertTrue(refreshed.isLiked)
            assertEquals(1, refreshed.likeCount)
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `두 건을 동기화할 때 하나만 실패하면 성공한 건만 정리되고 실패한 건만 남는다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse = feedsResponseOf(
                    feedResponse(feedId = 4L, isLiked = false, likeCount = 1),
                    feedResponse(feedId = 5L, isLiked = false, likeCount = 1),
                )
                repeat(2) { planLikeRequest("POST", 5L, outcome = LikeOutcome.FAIL_WITHOUT_APPLYING) }
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
            assertServerMatches(feedApi, feed4)
            assertFalse(
                feedApi.feedsResponse.feeds
                    .first { it.feedId == 5L }
                    .isLiked,
            )

            // 다시 동기화하면 실패했던 5만 다시 시도된다. 이 호출만으로 메모리와 저장소를 구분하지는 않는다.
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(4L, 5L, 5L), feedApi.postLikesCalls)
        }

    @Test
    fun `전송 중에 취소하면 화면의 취소가 유지되고 취소가 다시 대기열에 쌓인다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 6L, isLiked = false, likeCount = 0))
            }
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(6L)
            advanceUntilIdle()

            val postGate = CompletableDeferred<Unit>()
            feedApi.planLikeRequest("POST", 6L, responseGate = postGate)
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
            assertServerMatches(feedApi, repository.sosoAllFeeds.value.feed(6L))

            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(6L), feedApi.deleteLikesCalls) // 메모리도 비어 추가 요청이 없다
        }

    @Test
    fun `같은 방향 POST 두 개의 응답 순서가 바뀌어도 좋아요 상태와 개수가 유지된다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 7L, isLiked = false, likeCount = 0))
            }
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(7L)
            advanceUntilIdle()

            val firstPostGate = CompletableDeferred<Unit>()
            val firstPost = feedApi.planLikeRequest("POST", 7L, responseGate = firstPostGate)
            val secondPost = feedApi.planLikeRequest("POST", 7L)
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
            assertEquals(listOf(firstPost, secondPost), feedApi.appliedLikeRequests)
            assertEquals(listOf(secondPost, firstPost), feedApi.respondedLikeRequests)

            val finalFeed = repository.sosoAllFeeds.value.feed(7L)
            assertTrue(finalFeed.isLiked) // 화면의 마지막 선택: 손실 없이 좋아요 상태 유지
            assertEquals(1, finalFeed.likeCount)
            assertServerMatches(feedApi, finalFeed)
            assertTrue(pendingStore.currentPendingLikes().isEmpty()) // 뒤늦은 응답이 지울 대상이 없어도 안전하게 무시된다

            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(7L, 7L), feedApi.postLikesCalls) // 메모리도 비어 추가 요청이 없다
        }

    @Test
    fun `복원이 끝난 뒤 목록을 조회하면 복원된 pending이 즉시 반영된다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
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
            assertServerMatches(feedApi, repository.sosoAllFeeds.value.feed(1L))
        }

    @Test
    fun `복원이 끝나기 전에 목록을 조회해도 복원이 끝나면 목록에 뒤늦게 반영된다`() =
        runTest {
            val feedApi = FakeFeedApi().apply {
                feedsResponse =
                    feedsResponseOf(feedResponse(feedId = 1L, isLiked = false, likeCount = 3))
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

    private fun assertServerMatches(
        feedApi: FakeFeedApi,
        feed: FeedEntity,
    ) {
        val serverFeed = feedApi.feedsResponse.feeds.single { it.feedId == feed.id }
        assertEquals("서버와 화면의 좋아요 상태", feed.isLiked, serverFeed.isLiked)
        assertEquals("서버와 화면의 좋아요 개수", feed.likeCount, serverFeed.likeCount)
    }
}
