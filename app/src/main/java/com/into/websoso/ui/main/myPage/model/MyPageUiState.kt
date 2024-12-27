package com.into.websoso.ui.main.myPage.model

import com.into.websoso.data.model.MyProfileEntity

data class MyPageUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val myProfile: MyProfileEntity? = null,
)
