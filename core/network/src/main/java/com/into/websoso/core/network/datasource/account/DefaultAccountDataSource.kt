package com.into.websoso.core.network.datasource.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountEntity
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountDataSource
    @Inject
    constructor(
        retrofit: Retrofit,
    ) : AccountRemoteDataSource {
        private val accountApi by lazy { retrofit.create<AccountApi>() }

        override suspend fun postLogin(
            platform: AuthPlatform,
            authToken: AuthToken,
        ): AccountEntity =
            when (platform) {
                AuthPlatform.KAKAO -> accountApi.postLoginWithKakao(authToken.accessToken).toData()
            }
    }
