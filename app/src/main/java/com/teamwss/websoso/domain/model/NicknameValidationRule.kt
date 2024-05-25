package com.teamwss.websoso.domain.model

enum class NicknameValidationRule(val errorMessage: String, val validator: (String) -> Boolean) {
    START_OR_END_WITH_BLANK("공백으로 시작하거나 끝날 수 없어요", { it.trim() == it }),
    INVALID_LENGTH_OR_CHAR(
        "한글, 영문, 숫자, 특수문자(_,-) 2~10자까지 입력 가능해요",
        { NicknameValidator.VALID_NICKNAME_REGEX.matches(it) }),
    CONTAINS_FORBIDDEN_WORD("사용할 수 없는 단어가 포함되어 있어요 (금칙어)", { nickname ->
        !NicknameValidator.FORBIDDEN_WORDS.any { forbiddenWord -> nickname.contains(forbiddenWord) }
    });

    companion object {
        fun validate(nickname: String): ValidationResult {
            return values().firstOrNull { !it.validator(nickname) }?.let {
                ValidationResult(false, it.errorMessage)
            } ?: ValidationResult(true, "")
        }
    }
}