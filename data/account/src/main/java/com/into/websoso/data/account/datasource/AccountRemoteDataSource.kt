package com.into.websoso.data.account.datasource

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.model.AccountEntity
import com.into.websoso.data.account.model.TokenEntity

interface AccountRemoteDataSource {
    suspend fun postLogin(
        platform: AuthPlatform,
        authToken: AuthToken,
    ): AccountEntity

    suspend fun postLogout(
        refreshToken: String,
        deviceIdentifier: String,
    )

    suspend fun postReissue(refreshToken: String): TokenEntity
}
