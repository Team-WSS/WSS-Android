package com.teamwss.websoso.data.model

data class ProfileEntity(
    val avatarId: Int?,
    val nickname: String?,
    val intro: String?,
    val genrePreferences: List<String>
)
