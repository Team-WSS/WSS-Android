package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FCMTokenRequestDto(
    @SerialName("fcmToken")
    val fcmToken: String,
    @SerialName("deviceIdentifier")
    val deviceIdentifier: String,
)
