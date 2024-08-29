package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_KOREAN_CONSONANT_AND_VOWEL
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_LENGTH
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_NICKNAME_DUPLICATION
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_SPECIAL_CHARACTER
import com.teamwss.websoso.domain.model.NicknameValidationResult.NETWORK_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.UNKNOWN_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import javax.inject.Inject

class CheckNicknameValidityUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    private val invalidSpacingRegex = Regex("^\\s|\\s$")
    private val specialCharacterRegex = Regex("[^\\w가-힣-_]")
    private val koreanConsonantAndVowelRegex = Regex("[ㄱ-ㅎㅏ-ㅣ]")

    suspend operator fun invoke(nickname: String): NicknameValidationResult {
        val validationResult = getIsNicknameValid(nickname)

        return when {
            validationResult != VALID_NICKNAME -> validationResult
            else -> checkNicknameDuplication(nickname)
        }
    }

    private fun getIsNicknameValid(nickname: String): NicknameValidationResult {
        return when {
            nickname.length !in NICKNAME_MIN_LENGTH..NICKNAME_MAX_LENGTH -> INVALID_LENGTH
            invalidSpacingRegex.containsMatchIn(nickname) -> INVALID_LENGTH
            specialCharacterRegex.containsMatchIn(nickname) -> INVALID_SPECIAL_CHARACTER
            koreanConsonantAndVowelRegex.containsMatchIn(nickname) -> INVALID_KOREAN_CONSONANT_AND_VOWEL
            else -> VALID_NICKNAME
        }
    }

    private suspend fun checkNicknameDuplication(nickname: String): NicknameValidationResult {
        runCatching {
            userRepository.fetchNicknameValidity(nickname)
        }.onSuccess { isNicknameValid ->
            return if (isNicknameValid) VALID_NICKNAME
            else INVALID_NICKNAME_DUPLICATION
        }.onFailure {
            return NETWORK_ERROR
        }
        return UNKNOWN_ERROR
    }

    companion object {
        private const val NICKNAME_MIN_LENGTH = 2
        private const val NICKNAME_MAX_LENGTH = 10
    }
}
