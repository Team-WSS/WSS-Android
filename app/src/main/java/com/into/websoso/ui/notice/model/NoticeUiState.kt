package com.into.websoso.ui.notice.model

import com.into.websoso.domain.model.NoticeInfo

data class NoticeUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val noticeInfo: NoticeInfo = NoticeInfo(),
)
