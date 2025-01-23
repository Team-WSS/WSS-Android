package com.into.websoso.ui.notification.model

import com.into.websoso.domain.model.Notification

data class NotificationUiState(
    val isLoadable: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val lastNotificationId: Long = 0,
    val notifications: List<Notification> = emptyList(),
)
