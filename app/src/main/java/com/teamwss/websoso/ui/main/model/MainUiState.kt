package com.teamwss.websoso.ui.main.model

data class MainUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val nickname: String = "웹소소",
)