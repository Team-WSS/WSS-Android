package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelNotificationSettingResponseDto(
    @SerialName("isCompletionNotificationEnabled")
    val isCompletionNotificationEnabled: Boolean,
    @SerialName("isHiatusReturnNotificationEnabled")
    val isHiatusReturnNotificationEnabled: Boolean,
)
