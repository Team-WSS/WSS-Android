package com.teamwss.websoso.ui.onboarding.first.model

data class OnboardingFirstUiState(
    val nicknameInputType: NicknameInputType = NicknameInputType.INITIAL,
    val nicknameValidationMessage: String = "",
    val isNextButtonEnable: Boolean = false,
    val isDuplicationCheckButtonEnable: Boolean = false,
)
