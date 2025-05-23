package com.into.websoso.core.auth

data class AuthToken(
    val accessToken: String,
)

fun String.toAuthToken() =
    AuthToken(
        accessToken = this,
    )
