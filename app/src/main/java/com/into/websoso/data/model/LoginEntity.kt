package com.into.websoso.data.model

data class LoginEntity(
    val authorization: String,
    val refreshToken: String,
    val isRegister: Boolean,
)
