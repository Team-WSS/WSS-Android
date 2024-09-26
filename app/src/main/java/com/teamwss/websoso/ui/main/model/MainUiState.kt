package com.teamwss.websoso.ui.main.model

data class MainUiState(
    val isLogin: Boolean = true,
    val userId: Long = 0,
    val nickname: String = "웹소소",
)