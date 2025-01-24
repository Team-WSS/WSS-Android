package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushSettingResponseDto(
    @SerialName("isPushEnabled")
    val isPushEnabled: Boolean,
)
