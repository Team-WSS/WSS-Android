package com.teamwss.websoso.domain.model

data class Profile(
    val avatarId: Int,
    val nickname: String,
    val introduction: String,
    val genrePreferences: List<String>,
)
