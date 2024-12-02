package com.into.websoso.domain.usecase

import com.into.websoso.domain.model.NicknameValidationResult
import com.into.websoso.domain.model.NicknameValidationResult.INVALID_KOREAN_CONSONANT_AND_VOWEL
import com.into.websoso.domain.model.NicknameValidationResult.INVALID_LENGTH
import com.into.websoso.domain.model.NicknameValidationResult.INVALID_SPACING
import com.into.websoso.domain.model.NicknameValidationResult.INVALID_SPECIAL_CHARACTER
import com.into.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME_SPELLING
import javax.inject.Inject

class CheckNicknameValidityUseCase @Inject constructor() {

    private val invalidSpacingRegex = Regex("^\\s|\\s$")
    private val specialCharacterRegex = Regex("[^a-zA-Z0-9가-힣-_]")
    private val koreanConsonantAndVowelRegex = Regex("[ㄱ-ㅎㅏ-ㅣ]")

    operator fun invoke(nickname: String): NicknameValidationResult {
        return when {
            nickname.isValid() != VALID_NICKNAME_SPELLING -> nickname.isValid()
            else -> VALID_NICKNAME_SPELLING
        }
    }

    private fun String.isValid(): NicknameValidationResult {
        return when {
            length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH -> INVALID_LENGTH
            invalidSpacingRegex.containsMatchIn(this) -> INVALID_SPACING
            specialCharacterRegex.containsMatchIn(this) -> INVALID_SPECIAL_CHARACTER
            koreanConsonantAndVowelRegex.containsMatchIn(this) -> INVALID_KOREAN_CONSONANT_AND_VOWEL
            else -> VALID_NICKNAME_SPELLING
        }
    }

    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
    }
}
