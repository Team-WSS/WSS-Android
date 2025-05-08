package com.into.websoso.data.account

data class AccountEntity(
    val accessToken: String,
    val refreshToken: String,
    val isRegister: Boolean,
)
