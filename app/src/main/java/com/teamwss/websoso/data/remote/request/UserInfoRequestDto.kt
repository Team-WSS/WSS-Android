package com.teamwss.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoRequestDto(
    @SerialName("gender")
    val gender: String,
    @SerialName("birth")
    val birth: Int,
)
