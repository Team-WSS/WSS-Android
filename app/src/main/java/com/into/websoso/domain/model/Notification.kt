package com.into.websoso.domain.model

data class Notification(
    val notificationId: Long,
    val notificationIconImage: String,
    val notificationTitle: String,
    val notificationDescription: String,
    val createdDate: String,
    val isRead: Boolean,
    val isNotice: Boolean,
    val feedId: Long?,
    val intrinsicId: Long,
) {
    fun getNotificationType(): NotificationType =
        NotificationType.from(
            when {
                isNotice -> "NOTICE"
                feedId != null -> "FEED"
                else -> "NONE"
            },
        )
}
