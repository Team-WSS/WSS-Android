package com.into.websoso.core.auth

interface AuthClient {
    suspend fun login(): AuthToken

    suspend fun logout()

    suspend fun withdraw()
}
