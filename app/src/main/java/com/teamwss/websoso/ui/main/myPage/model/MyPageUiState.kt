package com.teamwss.websoso.ui.main.myPage.model

import com.teamwss.websoso.data.model.MyProfileEntity

data class MyPageUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val myProfile: MyProfileEntity? = null
)
