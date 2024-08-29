package com.teamwss.websoso.ui.profileEdit.model

import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.NONE
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME

data class ProfileEditUiState(
    val profile: ProfileModel = ProfileModel(),
    val previousProfile: ProfileModel = ProfileModel(),
    val isCheckDuplicateNicknameEnabled: Boolean = false,
    val nicknameEditResult: NicknameValidationResult = NONE,
    val profileEditResult: ProfileEditResult = ProfileEditResult.Loading,
) {
    val defaultState = this.nicknameEditResult != VALID_NICKNAME && this.nicknameEditResult != NONE
}
