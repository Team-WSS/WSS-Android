package com.into.websoso.domain.model

data class Notice(
    val id: Long,
    val noticeIconImage: String,
    val noticeTitle: String,
    val noticeType: NoticeType,
    val noticeDescription: String,
    val createdDate: String,
    val isRead: Boolean,
    val intrinsicId: Long?,
)
