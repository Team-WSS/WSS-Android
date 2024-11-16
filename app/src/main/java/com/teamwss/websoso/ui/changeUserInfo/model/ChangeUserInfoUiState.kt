package com.teamwss.websoso.ui.changeUserInfo.model

import com.teamwss.websoso.common.ui.model.Gender
import com.teamwss.websoso.common.ui.model.Gender.FEMALE

data class ChangeUserInfoUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val gender: Gender = FEMALE,
    val birthYear: Int = 2000,
    val isMaleButtonSelected: Boolean = false,
    val isFemaleButtonSelected: Boolean = false,
    val isCompleteButtonEnabled: Boolean = false,
    val isSaveStatusComplete: Boolean = false,
)
