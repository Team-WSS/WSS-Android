package com.into.websoso.ui.notice.model

import com.into.websoso.domain.model.Notice

data class NoticeUiState(
    val isLoadable: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val lastNoticeId: Long = 0,
    val notices: List<Notice> = emptyList(),
)
