package com.into.websoso.data.model

data class NotificationsEntity(
    val isLoadable: Boolean,
    val notifications: List<NotificationEntity>,
) {
    data class NotificationEntity(
        val notificationId: Long,
        val notificationImage: String,
        val notificationTitle: String,
        val notificationDescription: String,
        val createdDate: String,
        val isRead: Boolean,
        val isNotice: Boolean,
        val feedId: Long?,
    )
}
