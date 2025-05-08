package com.into.websoso.data.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import javax.inject.Inject

class AccountRepository
    @Inject
    constructor(
        private val accountRemoteDataSource: AccountRemoteDataSource,
        private val accountLocalDataSource: AccountLocalDataSource,
    ) {
        suspend fun accessToken(): String = accountLocalDataSource.accessToken()

        suspend fun refreshToken(): String = accountLocalDataSource.refreshToken()

        suspend fun saveToken(
            platform: AuthPlatform,
            authToken: AuthToken,
        ): Boolean {
            val account = accountRemoteDataSource.postLogin(
                platform = platform,
                authToken = authToken,
            )

            accountLocalDataSource.saveAccessToken(account.accessToken)
            accountLocalDataSource.saveRefreshToken(account.refreshToken)
            if (accountLocalDataSource.isAutoLogin().not()) accountLocalDataSource.saveIsAutoLogin(true)

            return account.isRegister
        }
    }
// TODO: 인스턴스 싱글톤 참고하기
