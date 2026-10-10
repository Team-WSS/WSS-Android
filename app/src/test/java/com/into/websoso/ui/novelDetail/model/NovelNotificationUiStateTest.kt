package com.into.websoso.ui.novelDetail.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelNotificationUiStateTest {
    @Test
    fun `초기 조회 중에는 변경할 수 없다`() {
        val uiState = NovelNotificationUiState(isLoading = true)

        assertFalse(uiState.isEditable)
    }

    @Test
    fun `초기 조회에 실패하면 변경할 수 없다`() {
        val uiState = NovelNotificationUiState(isLoading = false, isError = true)

        assertFalse(uiState.isEditable)
    }

    @Test
    fun `초기 조회에 성공해야 변경할 수 있다`() {
        val uiState = NovelNotificationUiState(isLoading = false, isError = false)

        assertTrue(uiState.isEditable)
    }
}
