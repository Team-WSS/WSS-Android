package com.teamwss.websoso.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNicknameValidityResponseDto(
    @SerialName("isValid")
    val isValid: Boolean,
)
