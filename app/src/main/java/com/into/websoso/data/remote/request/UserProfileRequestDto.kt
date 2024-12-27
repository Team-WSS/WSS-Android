package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileRequestDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("birth")
    val birth: Int,
    @SerialName("genrePreferences")
    val genrePreferences: List<String>,
)