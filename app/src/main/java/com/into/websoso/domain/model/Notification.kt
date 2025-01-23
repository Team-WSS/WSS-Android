package com.into.websoso.domain.model

data class Notification(
    val id: Long,
    val notificationIconImage: String,
    val notificationTitle: String,
    val notificationType: NotificationType,
    val notificationDescription: String,
    val createdDate: String,
    val isRead: Boolean,
    val intrinsicId: Long,
)
