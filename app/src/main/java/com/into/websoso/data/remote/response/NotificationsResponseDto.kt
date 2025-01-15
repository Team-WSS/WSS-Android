package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationsResponseDto(
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("notifications")
    val notifications: List<NotificationResponseDto>,
) {
    @Serializable
    data class NotificationResponseDto(
        @SerialName("notificationId")
        val notificationId: Long,
        @SerialName("notificationImage")
        val notificationImage: String,
        @SerialName("notificationTitle")
        val notificationTitle: String,
        @SerialName("notificationDescription")
        val notificationDescription: String,
        @SerialName("createdDate")
        val createdDate: String,
        @SerialName("isRead")
        val isRead: Boolean,
        @SerialName("isNotice")
        val isNotice: Boolean,
        @SerialName("feedId")
        val feedId: Long?,
    )
}
