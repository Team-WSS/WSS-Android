package com.teamwss.websoso.ui.profileEdit.model

import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.NONE
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.VALID_NICKNAME

data class ProfileEditUiState(
    val profile: ProfileModel = ProfileModel(),
    val previousProfile: ProfileModel = ProfileModel(),
    val isCheckDuplicateNicknameEnabled: Boolean = false,
    val nicknameEditResult: NicknameEditResult = NONE,
    val profileEditResult: ProfileEditResult = ProfileEditResult.Loading,
) {
    val defaultState = this.nicknameEditResult != VALID_NICKNAME && this.nicknameEditResult != NONE
}
