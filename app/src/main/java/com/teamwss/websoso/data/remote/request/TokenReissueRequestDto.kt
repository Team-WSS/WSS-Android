package com.teamwss.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenReissueRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String,
)
