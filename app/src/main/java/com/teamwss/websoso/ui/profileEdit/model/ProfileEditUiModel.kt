package com.teamwss.websoso.ui.profileEdit.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(
    val nicknameModel: NicknameModel = NicknameModel(),
    val introduction: String = "",
    val avatarId: Int = 0,
    val avatarImageUrl: String = "",
    val genrePreferences: List<Genre> = emptyList(),
) : Parcelable

@Parcelize
data class NicknameModel(
    val nickname: String = "",
    val hasFocus: Boolean = false,
) : Parcelable

enum class NicknameEditResult(
    val message: String,
) {
    INVALID_NICKNAME_DUPLICATION("이미 사용 중인 닉네임이에요"),
    INVALID_NICKNAME_LENGTH("한글, 영문, 숫자 2~10자까지 입력가능해요"),
    INVALID_NICKNAME_SPECIAL_CHARACTER("사용할 수 없는 단어가 포함되어 있어요"),
    VALID_NICKNAME(""),
    NONE(""),
}

sealed interface ProfileEditResult {
    object Loading : ProfileEditResult
    object Success : ProfileEditResult
    object Failure : ProfileEditResult
}
