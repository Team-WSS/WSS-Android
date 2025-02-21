package com.into.websoso.ui.notification.model

data class NotificationUiState(
    val isLoadable: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val lastNotificationId: Long = 0,
    val notifications: List<NotificationUiModel> = emptyList(),
)
