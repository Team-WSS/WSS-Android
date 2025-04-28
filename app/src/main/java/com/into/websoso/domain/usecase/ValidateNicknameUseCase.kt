package com.into.websoso.domain.usecase

import com.into.websoso.domain.model.ValidationResult
import javax.inject.Inject

class ValidateNicknameUseCase
    @Inject
    constructor() {
        operator fun invoke(nickname: String): ValidationResult =
            NicknameValidationRule.entries.firstOrNull { !it.validator(nickname) }?.let {
                ValidationResult(false, it.errorMessage)
            } ?: ValidationResult(true, "")

        private enum class NicknameValidationRule(
            val errorMessage: String,
            val validator: (String) -> Boolean,
        ) {
            START_OR_END_WITH_BLANK("공백은 포함될 수 없어요", { it.trim() == it }),
            INVALID_LENGTH_OR_CHAR(
                "한글, 영문, 숫자 2~10자까지 입력 가능해요",
                { "^[가-힣a-zA-Z0-9]{2,10}$".toRegex().matches(it) },
            ),
            CONTAINS_FORBIDDEN_WORD(
                "사용할 수 없는 단어가 포함되어 있어요\n(금칙어)",
                { nickname ->
                    !listOf("금지어1", "금지어2").any { forbiddenWord -> nickname.contains(forbiddenWord) }
                },
            ),
        }
    }
