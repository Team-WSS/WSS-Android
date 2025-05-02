package com.into.websoso.data.account

data class AccountEntity(
    val authorization: String,
    val refreshToken: String,
    val isRegister: Boolean,
)
