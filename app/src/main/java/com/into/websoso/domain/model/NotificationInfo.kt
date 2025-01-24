package com.into.websoso.domain.model

data class NotificationInfo(
    val isLoadable: Boolean = true,
    val lastNotificationId: Long = 0,
    val notifications: List<Notification> = emptyList(),
)
