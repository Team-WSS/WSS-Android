package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationPushEnabledResponseDto(
    @SerialName("isPushEnabled")
    val isPushEnabled: Boolean,
)
