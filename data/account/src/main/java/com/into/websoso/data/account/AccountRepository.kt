package com.into.websoso.data.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import javax.inject.Inject

class AccountRepository
    @Inject
    constructor(
        private val accountRemoteDataSource: AccountRemoteDataSource,
    ) {
        suspend fun saveToken(
            platform: AuthPlatform,
            authToken: AuthToken,
        ) {
            accountRemoteDataSource.postLogin(
                platform = platform,
                authToken = authToken,
            )
        }
    }
