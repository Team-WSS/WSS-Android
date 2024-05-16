package com.teamwss.websoso.ui.onBoarding.first.model

import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase

data class OnBoardingFirstUiState(
    val nicknameInputType: NicknameInputType = NicknameInputType.INITIAL,
    val nicknameValidationMessage: String = "",
    val nicknameValidationResult: ValidateNicknameUseCase.ValidationResult? = null,
    val isNextButtonEnable: Boolean = false,
    val isDuplicationCheckButtonEnable: Boolean = false,
)
