package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherUserProfileResponseDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("intro")
    val intro: String,
    @SerialName("avatarImage")
    val avatarImage: String,
    @SerialName("isProfilePublic")
    val isProfilePublic: Boolean,
    @SerialName("genrePreferences")
    val genrePreferences: List<String>,
)
