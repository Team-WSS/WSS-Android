package com.into.websoso.feature.collection

import com.into.websoso.core.resource.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionDetailUiStateTest {
    @Test
    fun `진입 직후와 응답 대기 중에는 오류 화면을 표시하지 않는다`() {
        val initial = CollectionDetailUiState()
        assertFalse(initial.showInitialError)
        assertFalse(initial.copy(isLoading = true).showInitialError)
    }

    @Test
    fun `실제 요청 실패에만 오류를 표시하고 재시도 중에는 숨긴다`() {
        val failed = CollectionDetailUiState(error = R.string.collection_load_failed)
        assertTrue(failed.showInitialError)
        assertFalse(failed.copy(isLoading = true).showInitialError)
        assertFalse(failed.copy(isLoading = true, error = null).showInitialError)
    }
}
