package com.into.websoso.data.model

data class NoticesEntity(
    val notices: List<NoticeEntity>,
) {
    data class NoticeEntity(
        val createDate: String,
        val noticeTitle: String,
        val noticeContent: String,
    )
}