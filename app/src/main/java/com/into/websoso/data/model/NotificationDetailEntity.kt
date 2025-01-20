package com.into.websoso.data.model

import java.time.LocalDate

data class NotificationDetailEntity(
    val notificationTitle: String,
    val notificationCreatedDate: String,
    val notificationDetail: String,
) {
    companion object {
        val DEFAULT = NotificationDetailEntity(
            notificationTitle = "웹소소 공지입니다",
            notificationCreatedDate = LocalDate.now().toString(),
            notificationDetail = "웹소소를 사랑해주셔서 감사합니다",
        )
    }
}
