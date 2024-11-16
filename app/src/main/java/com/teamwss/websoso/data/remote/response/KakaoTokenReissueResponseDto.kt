package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class KakaoTokenReissueResponseDto(
    @SerialName("Authorization")
    val authorization: String,
    @SerialName("refreshToken")
    val refreshToken: String,
)