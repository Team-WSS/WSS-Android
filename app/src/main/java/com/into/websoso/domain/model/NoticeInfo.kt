package com.into.websoso.domain.model

data class NoticeInfo(
    val isLoadable: Boolean = true,
    val lastNoticeId: Long = 0,
    val notices: List<Notice> = emptyList(),
)
