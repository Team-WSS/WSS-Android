package com.teamwss.websoso.domain.model

object NicknameValidator {
    val VALID_NICKNAME_REGEX by lazy { "^[\\w가-힣-]{2,10}$".toRegex() }
    val FORBIDDEN_WORDS by lazy { listOf("금지어1", "금지어2") }
}