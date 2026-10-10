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
    @Test
    fun `현재 계정 결함 기록 - A에서 실패한 pending이 B 목록에 반영되고 B 요청으로 전송된다`() =
        runTest {
            // 정상 기준은 계정 간 선택이 섞이지 않는 것이다. 아래는 현재 결함의 재현 예상값이다.
            // 실제 로그인/로그아웃 대신 API 대역의 계정 컨텍스트를 전환한다.
            val feedApi = accountAwareFeedApi(12L)
            feedApi.accountApis.getValue("A").planLikeRequest("POST", 12L, LikeOutcome.FAIL_WITHOUT_APPLYING)
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()

            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(12L)
            advanceUntilIdle()
            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("A", 12L, true)), feedApi.likeCalls)
            assertEquals(mapOf(12L to true), pendingStore.currentPendingLikes())
            assertFalse(feedApi.isLikedFor("A", 12L))

            feedApi.currentAccountId = null
            assertTrue(
                repository.sosoAllFeeds.value
                    .feed(12L)
                    .isLiked,
            )
            assertEquals(mapOf(12L to true), pendingStore.currentPendingLikes())
            feedApi.currentAccountId = "B"
            assertFalse(feedApi.isLikedFor("B", 12L))
            val refreshed = repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(12L)
            // B의 서버 원본은 false인데 A의 메모리 pending이 목록에 적용된다.
            assertTrue(refreshed.isLiked)
            assertEquals(1, refreshed.likeCount)

            repository.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(
                listOf(AccountLikeCall("A", 12L, true), AccountLikeCall("B", 12L, true)),
                feedApi.likeCalls,
            )
            assertFalse(feedApi.isLikedFor("A", 12L))
            assertTrue(feedApi.isLikedFor("B", 12L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `현재 계정 결함 기록 - 저장소만 비워도 A의 메모리 pending이 B 요청으로 전송된다`() =
        runTest {
            val feedApi = accountAwareFeedApi(13L)
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()
            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(13L)
            advanceUntilIdle()

            feedApi.currentAccountId = null
            // 실제 로그아웃 구현이 아니다. 저장소만 정리하는 대응으로 충분한지 분리해서 확인한다.
            pendingStore.deletePendingLike(13L)
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
            feedApi.currentAccountId = "B"
            repository.syncPendingLikes()
            advanceUntilIdle()

            assertEquals(listOf(AccountLikeCall("B", 13L, true)), feedApi.likeCalls)
            assertFalse(feedApi.isLikedFor("A", 13L))
            assertTrue(feedApi.isLikedFor("B", 13L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `현재 계정 결함 기록 - B에서 Repository를 재생성해도 저장된 A pending이 복원되어 전송된다`() =
        runTest {
            val feedApi = accountAwareFeedApi(14L)
            val pendingStore = FakePendingFeedLikeStore()
            val repositoryA = createRepository(feedApi, pendingStore)
            advanceUntilIdle()
            repositoryA.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryA.toggleLikeLocal(14L)
            advanceUntilIdle()
            assertEquals(mapOf(14L to true), pendingStore.currentPendingLikes())
            assertTrue(feedApi.likeCalls.isEmpty())

            feedApi.currentAccountId = null
            feedApi.currentAccountId = "B"
            // 같은 가짜 저장소를 읽는 새 인스턴스다. 실제 DataStore/프로세스 재실행 검증은 아니다.
            val repositoryB = createRepository(feedApi, pendingStore)
            advanceUntilIdle()
            val restored = repositoryB.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL").feeds.feed(14L)
            assertTrue(restored.isLiked)
            assertFalse(feedApi.isLikedFor("B", 14L))

            repositoryB.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("B", 14L, true)), feedApi.likeCalls)
            assertFalse(feedApi.isLikedFor("A", 14L))
            assertTrue(feedApi.isLikedFor("B", 14L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `현재 계정 결함 기록 - A에서 호출하고 실행을 기다리던 동기화가 B 요청으로 전송된다`() =
        runTest {
            val feedApi = accountAwareFeedApi(15L)
            val pendingStore = FakePendingFeedLikeStore()
            val repository = createRepository(feedApi, pendingStore)
            advanceUntilIdle()
            repository.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repository.toggleLikeLocal(15L)
            advanceUntilIdle()

            repository.syncPendingLikes()
            // launch된 동기화는 아직 실행하지 않고, 먼저 계정 컨텍스트를 바꾼다.
            assertTrue(feedApi.likeCalls.isEmpty())
            feedApi.currentAccountId = null
            feedApi.currentAccountId = "B"
            advanceUntilIdle()

            assertEquals(listOf(AccountLikeCall("B", 15L, true)), feedApi.likeCalls)
            assertFalse(feedApi.isLikedFor("A", 15L))
            assertTrue(feedApi.isLikedFor("B", 15L))
            assertTrue(pendingStore.currentPendingLikes().isEmpty())
        }

    @Test
    fun `현재 계정 결함 기록 - 저장소 정리 뒤 실행된 A의 저장이 B에서 복원되어 전송된다`() =
        runTest {
            val feedApi = accountAwareFeedApi(16L)
            val pendingStore = FakePendingFeedLikeStore()
            val repositoryA = createRepository(feedApi, pendingStore)
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
            // 저장 작업 실행 전 저장소 정리를 모의한다. 실제 로그아웃 구현은 아니다.
            pendingStore.deletePendingLike(16L)
            feedApi.currentAccountId = "B"
            advanceUntilIdle()
            assertEquals(mapOf(16L to true), pendingStore.currentPendingLikes())

            val repositoryB = createRepository(feedApi, pendingStore)
            advanceUntilIdle()
            repositoryB.fetchFeeds(lastFeedId = 0L, size = 10, feedsOption = "ALL")
            repositoryB.syncPendingLikes()
            advanceUntilIdle()
            assertEquals(listOf(AccountLikeCall("B", 16L, true)), feedApi.likeCalls)
            assertFalse(feedApi.isLikedFor("A", 16L))
            assertTrue(feedApi.isLikedFor("B", 16L))
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
