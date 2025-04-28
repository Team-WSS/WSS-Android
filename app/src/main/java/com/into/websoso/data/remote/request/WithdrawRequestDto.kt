package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawRequestDto(
    @SerialName("reason")
    val reason: String,
    @SerialName("refreshToken")
    val refreshToken: String,
)
