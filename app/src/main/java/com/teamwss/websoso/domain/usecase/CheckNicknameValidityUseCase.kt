package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult
import javax.inject.Inject

class CheckNicknameValidityUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    private val invalidLengthRegex = Regex("^\\s|\\s$")
    private val specialCharacterRegex = Regex("[^\\w가-힣-_]")
    private val hangulConsonantAndVowelRegex = Regex("[ㄱ-ㅎㅏ-ㅣ]")

    suspend fun execute(nickname: String): NicknameValidationResult {
        val validationResult = getIsNicknameValid(nickname)

        return when {
            validationResult != NicknameValidationResult.VALID_NICKNAME -> validationResult
            else -> checkNicknameDuplication(nickname)
        }
    }

    private fun getIsNicknameValid(nickname: String): NicknameValidationResult {
        return when {
            invalidLengthRegex.containsMatchIn(nickname) -> NicknameValidationResult.INVALID_LENGTH
            specialCharacterRegex.containsMatchIn(nickname) -> NicknameValidationResult.INVALID_SPECIAL_CHARACTER
            hangulConsonantAndVowelRegex.containsMatchIn(nickname) -> NicknameValidationResult.INVALID_KOREAN_CONSONANT_AND_VOWEL
            else -> NicknameValidationResult.VALID_NICKNAME
        }
    }

    private suspend fun checkNicknameDuplication(nickname: String): NicknameValidationResult {
        runCatching {
            userRepository.fetchNicknameValidity(nickname)
        }.onSuccess { isNicknameValid ->
            return if (isNicknameValid) NicknameValidationResult.VALID_NICKNAME
            else NicknameValidationResult.INVALID_NICKNAME_DUPLICATION
        }.onFailure {
            return NicknameValidationResult.NETWORK_ERROR
        }
        return NicknameValidationResult.UNKNOWN_ERROR
    }
}
