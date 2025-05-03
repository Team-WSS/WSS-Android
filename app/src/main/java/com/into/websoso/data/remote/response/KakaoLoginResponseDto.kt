package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class KakaoLoginResponseDto(
    @SerialName("Authorization")
    val authorization: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("isRegister")
    val isRegister: Boolean,
)
