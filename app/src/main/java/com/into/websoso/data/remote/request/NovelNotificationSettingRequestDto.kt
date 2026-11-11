package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelNotificationSettingRequestDto(
    @SerialName("isCompletionNotificationEnabled")
    val isCompletionNotificationEnabled: Boolean,
    @SerialName("isHiatusReturnNotificationEnabled")
    val isHiatusReturnNotificationEnabled: Boolean,
)
