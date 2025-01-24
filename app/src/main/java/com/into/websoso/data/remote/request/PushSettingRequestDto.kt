package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushSettingRequestDto(
    @SerialName("isPushEnabled")
    val isPushEnabled: Boolean,
)
