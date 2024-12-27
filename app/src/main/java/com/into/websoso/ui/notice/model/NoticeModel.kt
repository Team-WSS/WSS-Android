package com.into.websoso.ui.notice.model

import java.io.Serializable

data class NoticeModel(
    val createDate: String,
    val noticeTitle: String,
    val noticeContent: String,
) : Serializable
