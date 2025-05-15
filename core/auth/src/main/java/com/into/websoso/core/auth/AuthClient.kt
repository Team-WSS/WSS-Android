package com.into.websoso.core.auth

interface AuthClient {
    suspend fun signIn(): AuthToken

    suspend fun withdraw()
}
