package com.into.websoso.data.account.datasource

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountEntity

interface AccountRemoteDataSource {
    suspend fun postLogin(
        platform: AuthPlatform,
        authToken: AuthToken,
    ): AccountEntity
}
