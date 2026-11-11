package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatedFeedRepositoryAccountTest {
    // 계정 전환은 앱의 연결 지점과 같은 순서로 모의한다.
    // 로그아웃: discardPendingLikes(), 새 로그인 뒤 메인 진입: beginAccountCheck(), 사용자 정보 수신: confirmAccount().
    // API 대역의 계정 컨텍스트는 요청이 어느 계정의 토큰으로 나갔는지 기록하는 용도다.

    @Test
    fun `로그인 만료 뒤 B가 로그인하면 A의 실패 기록을 B 목록에 적용하지도 B 요청으로 보내지도 않는다`() =
        runTest {
            val feedApi = accountAwareFeedApi(12L)
            feedApi.accountApis.getValue("A").planLikeRequest("POST", 12L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repository = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(12L)
            advanceUntilIdle()
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("A", 12L, true)), feedApi.likeCalls)
            assertEquals(mapOf(12L to true), pendingStore.currentPendingLikes())
            assertFalse(feedApi.isLikedFor("A", 12L))

            // 로그인 만료로 로그인 화면에 갔다가 B가 로그인해 메인에 들어온다.
            feedApi.currentAccountId = null
            feedApi.currentAccountId = "B"
            repository.beginAccountCheck()
            val beforeConfirm = repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(12L)
            assertFalse(beforeConfirm.isLiked)
            assertEquals(0, beforeConfirm.likeCount)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("A", 12L, true)), feedApi.likeCalls)

            repository.confirmAccount(ACCOUNT_B)
            advanceUntilIdle()
            val refreshed = repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(12L)
            assertFalse(refreshed.isLiked)
            assertEquals(0, refreshed.likeCount)

            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("A", 12L, true)), feedApi.likeCalls)
            assertFalse(feedApi.isLikedFor("B", 12L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            assertTrue(repository.likeSyncStates.value.isEmpty())
        }

    @Test
    fun `로그아웃하면 A의 메모리 기록도 버려 B 요청으로 보내지 않는다`() =
        runTest {
            val feedApi = accountAwareFeedApi(13L)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repository = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(13L)
            advanceUntilIdle()
            assertEquals(mapOf(13L to true), pendingStore.currentPendingLikes())

            feedApi.currentAccountId = null
            repository.discardPendingLikes()
            advanceUntilIdle()
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            feedApi.currentAccountId = "B"
            repository.beginAccountCheck()
            repository.confirmAccount(ACCOUNT_B)
            repository.syncPendingLikes()
            advanceUntilIdle()

            assertTrue(feedApi.likeCalls.isEmpty())
            assertFalse(feedApi.isLikedFor("A", 13L))
            assertFalse(feedApi.isLikedFor("B", 13L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `B에서 Repository를 재생성하면 저장된 A 기록을 복원하지 않고 지운다`() =
        runTest {
            val feedApi = accountAwareFeedApi(14L)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repositoryA = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repositoryA.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryA.toggleLikeLocal(14L)
            advanceUntilIdle()
            assertEquals(mapOf(14L to true), pendingStore.currentPendingLikes())
            assertEquals(ACCOUNT_A, pendingStore.accountId)
            assertTrue(feedApi.likeCalls.isEmpty())

            feedApi.currentAccountId = null
            feedApi.currentAccountId = "B"
            // 같은 가짜 저장소를 읽는 새 인스턴스다. 실제 DataStore/프로세스 재실행 검증은 아니다.
            val repositoryB = createRepository(feedApi, pendingStore, accountId = null)
            advanceUntilIdle()
            val beforeConfirm = repositoryB.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(14L)
            assertFalse(beforeConfirm.isLiked)
            repositoryB.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(feedApi.likeCalls.isEmpty())

            repositoryB.confirmAccount(ACCOUNT_B)
            advanceUntilIdle()
            val restored = repositoryB.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(14L)
            assertFalse(restored.isLiked)
            repositoryB.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(feedApi.likeCalls.isEmpty())
            assertFalse(feedApi.isLikedFor("B", 14L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            assertEquals(ACCOUNT_B, pendingStore.accountId)
        }

    @Test
    fun `A에서 호출하고 실행을 기다리던 동기화는 B 로그인 뒤 보내지 않는다`() =
        runTest {
            val feedApi = accountAwareFeedApi(15L)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repository = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(15L)
            advanceUntilIdle()

            repository.syncPendingLikes()
            // launch된 동기화는 아직 실행하지 않고, 먼저 계정을 바꾼다.
            assertTrue(feedApi.likeCalls.isEmpty())
            feedApi.currentAccountId = null
            feedApi.currentAccountId = "B"
            repository.beginAccountCheck()
            advanceUntilIdle()
            assertTrue(feedApi.likeCalls.isEmpty())

            repository.confirmAccount(ACCOUNT_B)
            advanceUntilIdle()
            repository.syncPendingLikes()
            advanceUntilIdle()

            assertTrue(feedApi.likeCalls.isEmpty())
            assertFalse(feedApi.isLikedFor("A", 15L))
            assertFalse(feedApi.isLikedFor("B", 15L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `로그아웃 뒤 실행된 A의 저장 작업은 기록을 남기지 않아 B에서 복원되지 않는다`() =
        runTest {
            val feedApi = accountAwareFeedApi(16L)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repositoryA = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repositoryA.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryA.toggleLikeLocal(16L)
            // 화면과 메모리만 바뀐 시점이다. launch된 저장 작업은 아직 실행하지 않는다.
            assertTrue(
                repositoryA.sosoAllFeeds.value
                    .feed(16L)
                    .isLiked,
            )
            assertTrue(pendingStore.currentPendingLikes().isEmpty())

            feedApi.currentAccountId = null
            repositoryA.discardPendingLikes()
            feedApi.currentAccountId = "B"
            advanceUntilIdle()
            assertTrue(pendingStore.currentPendingLikes().isEmpty())

            val repositoryB = createRepository(feedApi, pendingStore, accountId = ACCOUNT_B)
            advanceUntilIdle()
            repositoryB.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryB.syncPendingLikes()
            advanceUntilIdle()
            assertTrue(feedApi.likeCalls.isEmpty())
            assertFalse(feedApi.isLikedFor("A", 16L))
            assertFalse(feedApi.isLikedFor("B", 16L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `로그인 만료 뒤 같은 계정이 다시 로그인하면 기록을 유지해 그 계정 요청으로 보낸다`() =
        runTest {
            val feedApi = accountAwareFeedApi(17L)
            feedApi.accountApis.getValue("A").planLikeRequest("POST", 17L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repository = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(17L)
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(mapOf(17L to true), pendingStore.currentPendingLikes())

            feedApi.currentAccountId = null
            feedApi.currentAccountId = "A"
            repository.beginAccountCheck()
            // 계정 확인 전에는 화면 적용과 전송을 보류한다.
            assertFalse(
                repository
                    .fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
                    .feeds
                    .feed(17L)
                    .isLiked,
            )
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("A", 17L, true)), feedApi.likeCalls)

            repository.confirmAccount(ACCOUNT_A)
            advanceUntilIdle()
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(17L)
                    .isLiked,
            )
            assertEquals(mapOf(17L to true), pendingStore.currentPendingLikes())
            repository.syncPendingLikes()
            advanceUntilIdle()

            assertEquals(
                listOf(AccountLikeCall("A", 17L, true), AccountLikeCall("A", 17L, true)),
                feedApi.likeCalls,
            )
            assertTrue(feedApi.isLikedFor("A", 17L))
            assertFalse(feedApi.isLikedFor("B", 17L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `앱을 다시 실행해 같은 계정이 확인되면 저장된 기록을 복원해 그 계정 요청으로 보낸다`() =
        runTest {
            val feedApi = accountAwareFeedApi(18L)
            val pendingStore = FakePendingFeedLikeStore(initialAccountId = null)
            val repositoryA = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()
            repositoryA.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryA.toggleLikeLocal(18L)
            advanceUntilIdle()

            val restartedRepository = createRepository(feedApi, pendingStore, accountId = null)
            advanceUntilIdle()
            restartedRepository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            restartedRepository.confirmAccount(ACCOUNT_A)
            advanceUntilIdle()
            assertTrue(
                restartedRepository.sosoAllFeeds.value
                    .feed(18L)
                    .isLiked,
            )
            restartedRepository.syncPendingLikes()
            advanceUntilIdle()

            assertEquals(listOf(AccountLikeCall("A", 18L, true)), feedApi.likeCalls)
            assertTrue(feedApi.isLikedFor("A", 18L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `계정 정보가 없는 이전 형식의 기록은 복원하지도 보내지도 않는다`() =
        runTest {
            val feedApi = accountAwareFeedApi(19L)
            val pendingStore = FakePendingFeedLikeStore(initial = mapOf(19L to true), initialAccountId = null)
            val repository = createRepository(feedApi, pendingStore, accountId = ACCOUNT_A)
            advanceUntilIdle()

            assertFalse(
                repository
                    .fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
                    .feeds
                    .feed(19L)
                    .isLiked,
            )
            repository.syncPendingLikes()
            advanceUntilIdle()

            assertTrue(feedApi.likeCalls.isEmpty())
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    private fun accountAwareFeedApi(feedId: Long): AccountAwareFeedApi =
        AccountAwareFeedApi(
            accountApis = listOf("A", "B").associateWith {
                FakeFeedApi().apply {
                    feedsResponse = feedsResponseOf(feedResponse(feedId, isLiked = false, likeCount = 0))
                }
            },
            currentAccountId = "A",
        )
}

private const val ACCOUNT_A = 1L
private const val ACCOUNT_B = 2L

private data class AccountLikeCall(
    val accountId: String,
    val feedId: Long,
    val isLiked: Boolean,
)

// 실제 로그인/인증을 실행하지 않고, 요청 시점의 계정 컨텍스트와 계정별 서버 상태를 모델링한다.
private class AccountAwareFeedApi(
    val accountApis: Map<String, FakeFeedApi>,
    var currentAccountId: String?,
) : FeedApi by accountApis.values.first() {
    val likeCalls = mutableListOf<AccountLikeCall>()

    fun isLikedFor(
        accountId: String,
        feedId: Long,
    ): Boolean =
        accountApis
            .getValue(accountId)
            .feedsResponse
            .feeds
            .single { it.feedId == feedId }
            .isLiked

    override suspend fun getFeeds(
        feedsOption: String,
        lastFeedId: Long,
        size: Int,
    ): FeedsResponseDto = accountApis.getValue(requireNotNull(currentAccountId)).getFeeds(feedsOption, lastFeedId, size)

    override suspend fun postLikes(feedId: Long) = sendLike(feedId, isLiked = true)

    override suspend fun deleteLikes(feedId: Long) = sendLike(feedId, isLiked = false)

    private suspend fun sendLike(
        feedId: Long,
        isLiked: Boolean,
    ) {
        val accountId = requireNotNull(currentAccountId)
        val api = accountApis.getValue(accountId)
        likeCalls += AccountLikeCall(accountId, feedId, isLiked)
        if (isLiked) api.postLikes(feedId) else api.deleteLikes(feedId)
    }
}
