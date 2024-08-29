package com.teamwss.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileEditRequestDto(
    @SerialName("avatarId")
    val avatarId: Int?,
    @SerialName("nickname")
    val nickname: String?,
    @SerialName("intro")
    val intro: String?,
    @SerialName("genrePreferences")
    val genrePreferences: List<String>,
)
