package com.teamwss.websoso.ui.main.model

data class MainUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isLogin: Boolean = true,
    val nickname: String = "웹소소",
)