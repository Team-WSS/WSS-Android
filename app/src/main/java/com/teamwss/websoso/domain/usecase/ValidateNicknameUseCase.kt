package com.teamwss.websoso.domain.usecase

class ValidateNicknameUseCase {

    fun getMaxNicknameLength(): Int {
        return MAX_NICKNAME_LENGTH
    }

    operator fun invoke(nickname: String): ValidationResult {
        if (nickname.trim() != nickname) {
            return ValidationResult(false, MESSAGES.START_OR_END_WITH_SPACE)
        }

        if (!REGEX_NICKNAME.matches(nickname)) {
            return ValidationResult(false, MESSAGES.INVALID_LENGTH_OR_CHAR)
        }

        if (FORBIDDEN_WORDS.any { nickname.contains(it) }) {
            return ValidationResult(false, MESSAGES.CONTAINS_FORBIDDEN_WORD)
        }

        return ValidationResult(true, "")
    }

    data class ValidationResult(val isSuccess: Boolean, val message: String)

    companion object {
        private const val MAX_NICKNAME_LENGTH = 10
        private val REGEX_NICKNAME = "^[\\w가-힣-]{2,$MAX_NICKNAME_LENGTH}$".toRegex()
        private val FORBIDDEN_WORDS = listOf("금지어1", "금지어2")

        private object MESSAGES {
            const val START_OR_END_WITH_SPACE = "공백으로 시작하거나 끝날 수 없어요"
            const val INVALID_LENGTH_OR_CHAR = "한글, 영문, 숫자, 특수문자(_,-) 2~10자까지 입력가능해요"
            const val CONTAINS_FORBIDDEN_WORD = "사용할 수 없는 단어가 포함되어 있어요 (금칙어)"
        }
    }
}
