package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.data.feed.repository.model.LikeSyncStatus
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryRecoveryTest {
    @Test
    fun `서버 반영 후 타임아웃과 취소에도 마지막 선택을 보존하고 수동 재시도로 복구한다`() =
        runTest {
            val api = server()
            api.planLikeRequest("POST", 1L, LikeOutcome.TIMEOUT_AFTER_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertTrue(repository.likeSyncStates.value.isEmpty())
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])

            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            assertFalse(
                repository
                    .fetchFeeds(0L, 10, "ALL")
                    .feeds
                    .single()
                    .isLiked,
            )
            assertEquals(
                0,
                repository.sosoAllFeeds.value
                    .single()
                    .likeCount,
            )
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
            assertFalse(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertTrue(store.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
        }

    @Test
    fun `응답 대기 중 취소한 뒤 타임아웃이 와도 취소 기록이 남는다`() =
        runTest {
            val api = server()
            val response = CompletableDeferred<Unit>()
            api.planLikeRequest("POST", 1L, LikeOutcome.TIMEOUT_AFTER_APPLYING, responseGate = response)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            runCurrent()
            assertEquals(LikeSyncStatus.SYNCING, repository.likeSyncStates.value[1L])
            repository.toggleLikeLocal(1L)
            runCurrent()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            response.complete(Unit)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
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
    fun `타임아웃 뒤 여러 번 눌러도 처음 상태와 같다는 이유로 기록을 지우지 않는다`() =
        runTest {
            val api = server()
            api.planLikeRequest("POST", 1L, LikeOutcome.TIMEOUT_AFTER_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            listOf(false, true, false).forEach { expected ->
                repository.toggleLikeLocal(1L)
                advanceUntilIdle()
                assertEquals(mapOf(1L to expected), store.currentPendingLikes())
                assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            }
        }

    @Test
    fun `중복 동기화와 취소가 겹쳐도 POST 하나가 끝난 뒤 최신 DELETE만 보낸다`() =
        runTest {
            val api = server()
            val postResponse = CompletableDeferred<Unit>()
            val deleteResponse = CompletableDeferred<Unit>()
            api.planLikeRequest("POST", 1L, responseGate = postResponse)
            api.planLikeRequest("DELETE", 1L, responseGate = deleteResponse)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            runCurrent()
            repository.syncPendingLikes()
            runCurrent()
            assertEquals(listOf(1L), api.postLikesCalls)
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            runCurrent()
            assertTrue(api.deleteLikesCalls.isEmpty())
            postResponse.complete(Unit)
            runCurrent()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertEquals(listOf(1L), api.deleteLikesCalls)
            assertFalse(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            deleteResponse.complete(Unit)
            advanceUntilIdle()
            assertFalse(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertEquals(
                0,
                api.feedsResponse.feeds
                    .single()
                    .likeCount,
            )
            assertTrue(store.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.deleteLikesCalls)
        }

    @Test
    fun `실패하는 요청에 동기화 호출이 겹쳐도 즉시 재시도하지 않는다`() =
        runTest {
            val api = server()
            val response = CompletableDeferred<Unit>()
            api.planLikeRequest("POST", 1L, LikeOutcome.FAIL_WITHOUT_APPLYING, responseGate = response)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            runCurrent()
            repository.syncPendingLikes()
            runCurrent()
            response.complete(Unit)
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertEquals(mapOf(1L to true), store.currentPendingLikes())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L, 1L), api.postLikesCalls)
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `실패 항목 수동 재시도와 화면 종료가 겹쳐도 한 번만 전송한다`() =
        runTest {
            val api = server()
            api.planLikeRequest("POST", 1L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            val response = CompletableDeferred<Unit>()
            api.planLikeRequest("POST", 1L, responseGate = response)
            repository.retryPendingLikes()
            runCurrent()
            repository.retryPendingLikes()
            repository.syncPendingLikes()
            runCurrent()
            assertEquals(listOf(1L, 1L), api.postLikesCalls)
            response.complete(Unit)
            advanceUntilIdle()
            assertEquals(listOf(1L, 1L), api.postLikesCalls)
            assertTrue(store.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `수동 재시도는 실패 항목만 보내고 화면 종료는 새 선택도 보낸다`() =
        runTest {
            val api = server()
            api.feedsResponse = feedsResponseOf(feedResponse(1L, false, 0), feedResponse(2L, false, 0))
            api.planLikeRequest("POST", 1L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            repository.toggleLikeLocal(2L)
            advanceUntilIdle()
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L, 1L), api.postLikesCalls)
            assertEquals(mapOf(2L to true), store.currentPendingLikes())
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L, 1L, 2L), api.postLikesCalls)
            assertTrue(api.feedsResponse.feeds.all { it.isLiked && it.likeCount == 1 })
        }

    @Test
    fun `이전 재시도가 성공해도 그동안 바뀐 최신 선택의 안내를 지우지 않는다`() =
        runTest {
            val api = server()
            api.planLikeRequest("POST", 1L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            val response = CompletableDeferred<Unit>()
            api.planLikeRequest("POST", 1L, responseGate = response)
            repository.retryPendingLikes()
            runCurrent()
            repository.toggleLikeLocal(1L)
            response.complete(Unit)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            assertTrue(api.deleteLikesCalls.isEmpty())
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertFalse(
                api.feedsResponse.feeds
                    .single()
                    .isLiked,
            )
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `요청 취소도 결과 미확정으로 남겨 이후 취소 선택을 보존한다`() =
        runTest {
            val api = object : FeedApi by server() {
                override suspend fun postLikes(feedId: Long): Unit = throw CancellationException("cancelled response")
            }
            val store = FakePendingFeedLikeStore()
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertEquals(mapOf(1L to false), store.currentPendingLikes())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
        }

    @Test
    fun `저장에 실패하면 서버로 보내지 않고 안내를 남겨 다시 시도할 수 있다`() =
        runTest {
            val api = server()
            val backing = FakePendingFeedLikeStore()
            var failSave = true
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun updatePendingLike(
                    feedId: Long,
                    isLiked: Boolean,
                ) {
                    if (failSave) throw IOException("test storage unavailable")
                    backing.updatePendingLike(feedId, isLiked)
                }
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(api.postLikesCalls.isEmpty())
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            failSave = false
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `성공 후 저장 기록 삭제만 실패하면 재시도는 서버 요청을 반복하지 않는다`() =
        runTest {
            val api = server()
            val backing = FakePendingFeedLikeStore()
            var failDelete = true
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun deletePendingLike(feedId: Long) {
                    if (failDelete) throw IOException("test storage unavailable")
                    backing.deletePendingLike(feedId)
                }
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertEquals(LikeSyncStatus.NEEDS_RETRY, repository.likeSyncStates.value[1L])
            failDelete = false
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertTrue(backing.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `저장된 좋아요를 읽지 못해도 예외 없이 저장과 전송을 보류한다`() =
        runTest {
            val api = server()
            val backing = FakePendingFeedLikeStore()
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun getPendingLikes(): Map<Long, Boolean> = throw IOException("test storage unavailable")
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertTrue(backing.updateCalls.isEmpty())
            assertTrue(api.postLikesCalls.isEmpty())
            assertTrue(repository.isLikeRestoreFailed.value)
        }

    @Test
    fun `저장소를 다시 읽을 수 있게 되면 다시 시도로 복원하고 이어서 전송한다`() =
        runTest {
            val api = server()
            val backing = FakePendingFeedLikeStore(initial = mapOf(1L to true))
            var failRead = true
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun getPendingLikes(): Map<Long, Boolean> {
                    if (failRead) throw IOException("test storage unavailable")
                    return backing.getPendingLikes()
                }
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertFalse(
                repository.sosoAllFeeds.value
                    .single()
                    .isLiked,
            )
            assertTrue(api.postLikesCalls.isEmpty())
            assertTrue(repository.isLikeRestoreFailed.value)
            failRead = false
            repository.retryPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertTrue(backing.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
            assertFalse(repository.isLikeRestoreFailed.value)
        }

    @Test
    fun `손상된 저장 파일이 초기화됐으면 복원 후 미복원 안내 상태를 남기고 이어서 동작한다`() =
        runTest {
            val api = server()
            val store = FakePendingFeedLikeStore().apply { resetNotice = true }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            assertTrue(repository.hasUnrestoredLikes.value)
            assertTrue(store.resetNotice)

            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(1L), api.postLikesCalls)
            assertTrue(store.currentPendingLikes().isEmpty())
        }

    @Test
    fun `미복원 안내 표시는 봤음 처리 전까지 저장소를 다시 만들어도 남는다`() =
        runTest {
            val api = server()
            val store = FakePendingFeedLikeStore().apply { resetNotice = true }
            createRepository(api, store)
            advanceUntilIdle()

            val recreated = createRepository(api, store)
            advanceUntilIdle()
            assertTrue(recreated.hasUnrestoredLikes.value)

            recreated.acknowledgeUnrestoredLikes()
            advanceUntilIdle()
            assertFalse(recreated.hasUnrestoredLikes.value)
            assertFalse(store.resetNotice)

            val afterAcknowledged = createRepository(api, store)
            advanceUntilIdle()
            assertFalse(afterAcknowledged.hasUnrestoredLikes.value)
        }

    @Test
    fun `복원에 실패한 동안 누른 좋아요는 복원에 성공하면 함께 저장된다`() =
        runTest {
            val api = FakeFeedApi().apply {
                feedsResponse = feedsResponseOf(feedResponse(1L, false, 0), feedResponse(2L, false, 0))
            }
            val backing = FakePendingFeedLikeStore()
            var failRead = true
            val store = object : PendingFeedLikeStore by backing {
                override suspend fun getPendingLikes(): Map<Long, Boolean> {
                    if (failRead) throw IOException("test storage unavailable")
                    return backing.getPendingLikes()
                }
            }
            val repository = createRepository(api, store)
            advanceUntilIdle()
            repository.fetchFeeds(0L, 10, "ALL")
            repository.toggleLikeLocal(1L)
            advanceUntilIdle()
            assertTrue(backing.currentPendingLikes().isEmpty())

            failRead = false
            repository.toggleLikeLocal(2L)
            advanceUntilIdle()
            assertEquals(mapOf(1L to true, 2L to true), backing.currentPendingLikes())
        }

    private fun server() =
        FakeFeedApi().apply {
            feedsResponse = feedsResponseOf(feedResponse(1L, false, 0))
        }
}
