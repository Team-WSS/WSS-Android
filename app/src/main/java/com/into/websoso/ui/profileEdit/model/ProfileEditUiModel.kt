package com.into.websoso.ui.profileEdit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(
    val nicknameModel: NicknameModel = NicknameModel(),
    val introduction: String = "",
    val avatarId: Int = 0,
    val avatarThumbnail: String = "",
    val genrePreferences: List<Genre> = emptyList(),
) : Parcelable

@Parcelize
data class NicknameModel(
    val nickname: String = "",
    val hasFocus: Boolean = false,
) : Parcelable

sealed interface ProfileEditResult {
    data object Loading : ProfileEditResult
    data object Success : ProfileEditResult
    data object Error : ProfileEditResult
}

sealed interface LoadProfileResult {
    data object Success : LoadProfileResult
    data object Loading : LoadProfileResult
    data object Error : LoadProfileResult
}
