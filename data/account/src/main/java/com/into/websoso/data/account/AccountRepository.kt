package com.into.websoso.data.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository
    @Inject
    constructor(
        private val accountRemoteDataSource: AccountRemoteDataSource,
        private val accountLocalDataSource: AccountLocalDataSource,
    ) {
        // TODO: UserRepository로 이동
        var isRegisterUser: Boolean = false
            private set

        suspend fun accessToken(): String = accountLocalDataSource.accessToken()

        suspend fun refreshToken(): String = accountLocalDataSource.refreshToken()

        suspend fun saveTokens(
            platform: AuthPlatform,
            authToken: AuthToken,
        ): Result<Unit> =
            runCatching {
                val account = accountRemoteDataSource.postLogin(
                    platform = platform,
                    authToken = authToken,
                )

                accountLocalDataSource.saveAccessToken(account.token.accessToken)
                accountLocalDataSource.saveRefreshToken(account.token.refreshToken)
                isRegisterUser = account.isRegister
            }

        suspend fun deleteTokens(deviceIdentifier: String): Result<Unit> =
            runCatching {
                accountRemoteDataSource
                    .postLogout(
                        refreshToken = refreshToken(),
                        deviceIdentifier = deviceIdentifier,
                    )

                accountLocalDataSource.clearTokens()
            }

        suspend fun deleteAccount(reason: String): Result<Unit> =
            runCatching {
                accountRemoteDataSource.postWithdraw(reason = reason)
                accountLocalDataSource.clearTokens()
            }

        suspend fun renewTokens(): Result<Unit> =
            runCatching {
                val tokens = accountRemoteDataSource.postReissue(refreshToken = refreshToken())

                accountLocalDataSource.saveAccessToken(tokens.accessToken)
                accountLocalDataSource.saveRefreshToken(tokens.refreshToken)
            }
    }
