package com.into.websoso.ui.novelDetail.model

data class NovelNotificationUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isCompletionNotificationEnabled: Boolean = false,
    val isHiatusReturnNotificationEnabled: Boolean = false,
)
