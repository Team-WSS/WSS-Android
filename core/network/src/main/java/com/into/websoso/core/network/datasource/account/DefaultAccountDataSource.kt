package com.into.websoso.core.network.datasource.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultAccountDataSource
    @Inject
    constructor(
        private val accountApi: AccountApi,
    ) : AccountRemoteDataSource {
        override suspend fun postLogin(
            platform: AuthPlatform,
            authToken: AuthToken,
        ) = when (platform) {
            AuthPlatform.KAKAO -> accountApi.postLoginWithKakao(authToken.accessToken).toData()
        }
    }
