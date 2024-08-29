package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_KOREAN_CONSONANT_AND_VOWEL
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_LENGTH
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_SPACING
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_SPECIAL_CHARACTER
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import javax.inject.Inject

class CheckNicknameValidityUseCase @Inject constructor() {

    private val invalidSpacingRegex = Regex("^\\s|\\s$")
    private val specialCharacterRegex = Regex("[^\\w가-힣-_]")
    private val koreanConsonantAndVowelRegex = Regex("[ㄱ-ㅎㅏ-ㅣ]")

    operator fun invoke(nickname: String): NicknameValidationResult {
        return when {
            nickname.isValid() != VALID_NICKNAME -> nickname.isValid()
            else -> VALID_NICKNAME
        }
    }

    private fun String.isValid(): NicknameValidationResult {
        return when {
            length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH -> INVALID_LENGTH
            invalidSpacingRegex.containsMatchIn(this) -> INVALID_SPACING
            specialCharacterRegex.containsMatchIn(this) -> INVALID_SPECIAL_CHARACTER
            koreanConsonantAndVowelRegex.containsMatchIn(this) -> INVALID_KOREAN_CONSONANT_AND_VOWEL
            else -> VALID_NICKNAME
        }
    }

    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
    }
}
