package com.into.websoso.data.account.datasource

interface AccountLocalDataSource {
    suspend fun accessToken(): String

    suspend fun refreshToken(): String

    suspend fun saveAccessToken(accessToken: String)

    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun clearTokens()
}
