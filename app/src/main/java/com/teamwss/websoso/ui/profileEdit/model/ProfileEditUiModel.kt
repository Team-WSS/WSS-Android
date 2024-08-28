package com.teamwss.websoso.ui.profileEdit.model

import java.io.Serializable

data class ProfileModel(
    val nicknameModel: NicknameModel = NicknameModel(),
    val introduction: String = "",
    val avatarId: Int = 0,
    val avatarImageUrl: String = "",
    val genrePreferences: List<Genre> = emptyList(),
) : Serializable

data class NicknameModel(
    val nickname: String = "",
    val hasFocus: Boolean = false,
) : Serializable

enum class NicknameEditResult(
    val message: String,
) {
    INVALID_NICKNAME_DUPLICATION("이미 사용 중인 닉네임이에요"),
    INVALID_NICKNAME_LENGTH("한글, 영문, 숫자 2~10자까지 입력가능해요"),
    INVALID_NICKNAME_SPECIAL_CHARACTER("사용할 수 없는 단어가 포함되어 있어요"),
    VALID_NICKNAME(""),
    NONE(""),
    ;

    companion object {
        fun String.checkNicknameValidity(): NicknameEditResult {
            return when {
                this.length !in 2..10 -> INVALID_NICKNAME_LENGTH
                this.contains(Regex("^\\s|\\s$|[^\\w가-힣-_]|[ㄱ-ㅎㅏ-ㅣ]")) -> INVALID_NICKNAME_SPECIAL_CHARACTER
                else -> VALID_NICKNAME
            }
        }
    }
}

sealed interface ProfileEditResult {
    object Loading : ProfileEditResult
    object Success : ProfileEditResult
    object Failure : ProfileEditResult
}