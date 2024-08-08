package com.teamwss.websoso.ui.notice.model

data class NoticeUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val notices : List<NoticeModel> = emptyList(),
)
