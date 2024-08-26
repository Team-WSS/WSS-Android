package com.teamwss.websoso.ui.changeUserInfo.model

data class ChangeUserInfoUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val gender: Gender? = null,
    val birthYear: Int? = null,
    val isMaleButtonSelected: Boolean = false,
    val isFemaleButtonSelected: Boolean = false,
    val isCompleteButtonEnabled: Boolean = false,
    val isSaveStatusComplete: Boolean = false,
)
