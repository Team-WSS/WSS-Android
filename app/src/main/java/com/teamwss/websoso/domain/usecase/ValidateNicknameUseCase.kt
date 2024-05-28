package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.domain.model.NicknameValidationRule
import com.teamwss.websoso.domain.model.ValidationResult
import javax.inject.Inject

class ValidateNicknameUseCase @Inject constructor() {

    operator fun invoke(nickname: String): ValidationResult {
        return NicknameValidationRule.validate(nickname)
    }
}