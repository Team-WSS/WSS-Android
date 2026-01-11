package com.into.websoso.user.model

data class MyProfileEntity(
    val nickname: String,
    val intro: String,
    val avatarImage: String,
    val genrePreferences: List<String>,
)
