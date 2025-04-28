package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinimumVersionResponseDto(
    @SerialName("minimumVersion")
    val minimumVersion: String,
    @SerialName("updateDate")
    val updateDate: String,
)
