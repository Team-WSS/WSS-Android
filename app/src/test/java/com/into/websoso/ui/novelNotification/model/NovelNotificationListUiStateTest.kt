package com.into.websoso.ui.novelNotification.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelNotificationListUiStateTest {
    @Test
    fun `초기 조회 전에는 빈 화면으로 보지 않는다`() {
        val uiState = NovelNotificationListUiState(isInitialLoaded = false)

        assertFalse(uiState.isEmpty)
    }

    @Test
    fun `초기 조회 후 목록이 비어야 빈 화면이다`() {
        val uiState = NovelNotificationListUiState(isInitialLoaded = true)

        assertTrue(uiState.isEmpty)
    }

    @Test
    fun `조회에 실패하면 빈 화면이 아니라 오류 화면으로 본다`() {
        val uiState = NovelNotificationListUiState(isInitialLoaded = true, isError = true)

        assertFalse(uiState.isEmpty)
        assertTrue(uiState.isErrorVisible)
    }

    @Test
    fun `재시도 중에는 빈 화면으로 보지 않는다`() {
        val uiState = NovelNotificationListUiState(isInitialLoaded = true, isLoading = true)

        assertFalse(uiState.isEmpty)
    }

    @Test
    fun `이미 불러온 항목이 있으면 다음 페이지가 실패해도 오류 화면으로 덮지 않는다`() {
        val uiState = NovelNotificationListUiState(
            isInitialLoaded = true,
            isError = true,
            subscriptions = listOf(subscription(1)),
        )

        assertFalse(uiState.isErrorVisible)
    }

    @Test
    fun `목록이 비어 있으면 앱바 액션을 노출하지 않는다`() {
        assertFalse(NovelNotificationListUiState(isInitialLoaded = true).isActionVisible)
        assertFalse(NovelNotificationListUiState(isInitialLoaded = true, isError = true).isActionVisible)
        assertTrue(NovelNotificationListUiState(subscriptions = listOf(subscription(1))).isActionVisible)
    }

    @Test
    fun `선택한 작품이 없으면 삭제할 수 없다`() {
        val uiState = NovelNotificationListUiState(subscriptions = listOf(subscription(1)))

        assertFalse(uiState.isDeletable)
    }

    @Test
    fun `삭제 알럿은 목록 순서가 아닌 선택 순서의 첫 작품을 사용한다`() {
        val uiState = NovelNotificationListUiState(
            subscriptions = listOf(subscription(1), subscription(2), subscription(3)),
            // 목록상 세 번째 작품을 가장 먼저 선택했다
            selectedNovelIds = linkedSetOf(3L, 1L),
        )

        assertTrue(uiState.isDeletable)
        assertEquals(2, uiState.selectedSubscriptions.size)
        assertEquals("작품 3", uiState.selectedSubscriptions.first().novelTitle)
    }

    private fun subscription(novelId: Long): NovelNotificationSubscriptionModel =
        NovelNotificationSubscriptionModel(
            subscriptionId = novelId,
            novelId = novelId,
            novelTitle = "작품 $novelId",
            novelAuthor = "작가",
            novelImage = "",
            registeredDate = "2026.08.27",
        )
}
