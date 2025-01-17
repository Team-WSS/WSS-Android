package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationUnreadResponseDto(
    @SerialName("hasUnreadNotifications")
    val hasUnreadNotifications: Boolean,
)
