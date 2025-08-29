package com.into.websoso.data.account.datasource

interface AccountLocalDataSource {
    suspend fun selectAccessToken(): String

    suspend fun selectRefreshToken(): String

    suspend fun updateAccessToken(accessToken: String)

    suspend fun updateRefreshToken(refreshToken: String)

    suspend fun deleteTokens()
}
