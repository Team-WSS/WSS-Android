package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
)
