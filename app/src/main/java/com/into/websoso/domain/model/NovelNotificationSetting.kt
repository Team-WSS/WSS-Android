package com.into.websoso.domain.model

data class NovelNotificationSetting(
    val isCompletionNotificationEnabled: Boolean = false,
    val isHiatusReturnNotificationEnabled: Boolean = false,
)
