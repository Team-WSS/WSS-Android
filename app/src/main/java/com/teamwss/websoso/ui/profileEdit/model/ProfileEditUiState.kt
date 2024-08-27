package com.teamwss.websoso.ui.profileEdit.model

data class ProfileEditUiState(
    val profile: ProfileModel = ProfileModel(),
    val previousProfile: ProfileModel = ProfileModel(),
    val nicknameEditResult: NicknameEditResult = NicknameEditResult.NONE,
)
