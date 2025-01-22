package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDetailResponseDto(
    @SerialName("notificationTitle")
    val notificationTitle: String,
    @SerialName("notificationCreatedDate")
    val notificationCreatedDate: String,
    @SerialName("notificationDetail")
    val notificationDetail: String,
)
