package com.teamwss.websoso.ui.onBoarding.first.model

data class OnBoardingFirstUiState(
    val nicknameInputType: NicknameInputType = NicknameInputType.INITIAL,
    val nicknameValidationMessage: String = "",
    val isNextButtonEnable: Boolean = false,
    val isDuplicationCheckButtonEnable: Boolean = false,
)
