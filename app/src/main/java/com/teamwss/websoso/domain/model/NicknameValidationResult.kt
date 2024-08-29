package com.teamwss.websoso.domain.model

enum class NicknameValidationResult {
    VALID_NICKNAME,
    INVALID_LENGTH,
    INVALID_SPACING,
    INVALID_SPECIAL_CHARACTER,
    INVALID_KOREAN_CONSONANT_AND_VOWEL,
    INVALID_NICKNAME_DUPLICATION,
    NETWORK_ERROR,
    UNKNOWN_ERROR,
    NONE,
    ;

    companion object {
        fun NicknameValidationResult.getProfileEditMessage(): String {
            return when (this) {
                VALID_NICKNAME -> "사용 가능한 닉네임이에요"
                INVALID_LENGTH -> "한글, 영문, 숫자 2~10자까지 입력가능해요"
                INVALID_SPACING -> "사용할 수 없는 단어가 포함되어 있어요"
                INVALID_SPECIAL_CHARACTER -> "사용할 수 없는 단어가 포함되어 있어요"
                INVALID_KOREAN_CONSONANT_AND_VOWEL -> "사용할 수 없는 단어가 포함되어 있어요"
                INVALID_NICKNAME_DUPLICATION -> "이미 사용 중인 닉네임이에요"
                NETWORK_ERROR -> "네트워크 오류가 발생했어요"
                UNKNOWN_ERROR -> "알 수 없는 오류가 발생했어요"
                NONE -> ""
            }
        }
    }
}
