package com.teamwss.websoso.ui.onboarding.model

data class UserModel(
    val nickname : String = "",
    val gender: String = "",
    val birthYear: Int = 0,
    val genrePreferences: List<String> = listOf(),
)