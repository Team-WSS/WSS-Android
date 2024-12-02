package com.into.websoso.ui.profileEdit.model

import com.into.websoso.domain.model.NicknameValidationResult
import com.into.websoso.domain.model.NicknameValidationResult.NONE
import com.into.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME

data class ProfileEditUiState(
    val profile: ProfileModel = ProfileModel(),
    val previousProfile: ProfileModel = ProfileModel(),
    val isCheckDuplicateNicknameEnabled: Boolean = false,
    val nicknameEditResult: NicknameValidationResult = NONE,
    val profileEditResult: ProfileEditResult = ProfileEditResult.Loading,
    val loadProfileResult: LoadProfileResult = LoadProfileResult.Loading,
) {
    val defaultState = nicknameEditResult != VALID_NICKNAME && nicknameEditResult != NONE
    val isFinishButtonEnabled = when {
        profile == previousProfile -> false
        nicknameEditResult == VALID_NICKNAME -> true
        profile.nicknameModel.nickname == previousProfile.nicknameModel.nickname -> true
        else -> false
    }
}
