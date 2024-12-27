package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoticesResponseDto(
    @SerialName("notices")
    val notices: List<NoticeResponseDto>,
) {
    @Serializable
    data class NoticeResponseDto(
        @SerialName("createdDate")
        val createdDate: String,
        @SerialName("noticeContent")
        val noticeContent: String,
        @SerialName("noticeTitle")
        val noticeTitle: String,
    )
}