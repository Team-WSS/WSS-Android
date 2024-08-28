package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyProfileResponseDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("intro")
    val intro: String,
    @SerialName("avatarImage")
    val avatarImage: String,
    @SerialName("genrePreferences")
    val genrePreferences: List<String>
)