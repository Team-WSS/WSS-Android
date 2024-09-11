package com.teamwss.websoso.data.model

data class OtherUserProfileEntity(
    val nickname: String,
    val intro: String,
    val avatarImage: String,
    val isProfilePublic: Boolean,
    val genrePreferences: List<String>,
)