package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEmailResponseDto(
    @SerialName("email")
    val email: String,
)
