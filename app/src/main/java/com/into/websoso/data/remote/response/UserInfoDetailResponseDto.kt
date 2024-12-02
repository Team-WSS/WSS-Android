package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDetailResponseDto(
    @SerialName("email")
    val email: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("birth")
    val birth: Int,
)
