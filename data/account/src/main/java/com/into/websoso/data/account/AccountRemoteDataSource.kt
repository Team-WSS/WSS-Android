package com.into.websoso.data.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken

interface AccountRemoteDataSource {
    suspend fun postLogin(
        platform: AuthPlatform,
        authToken: AuthToken,
    ): AccountEntity
}
