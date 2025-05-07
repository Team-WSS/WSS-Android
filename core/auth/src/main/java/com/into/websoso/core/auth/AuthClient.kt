package com.into.websoso.core.auth

interface AuthClient {
    suspend fun signIn(): AuthToken

    suspend fun signOut()

    suspend fun withdraw()
}
