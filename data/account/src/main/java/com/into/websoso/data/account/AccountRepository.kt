package com.into.websoso.data.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

        private val _userId = MutableStateFlow(0L)

        val userIdFlow: StateFlow<Long> = _userId.asStateFlow()

        val userId: Long
            get() = _userId.value

        fun updateUserId(userId: Long) {
            _userId.value = userId
        }

        suspend fun accessToken(): String = accountLocalDataSource.selectAccessToken()

        suspend fun refreshToken(): String = accountLocalDataSource.selectRefreshToken()

        suspend fun createAccount(
            platform: AuthPlatform,
            authToken: AuthToken,
        ): Result<Unit> =
            runCatching {
                val account = accountRemoteDataSource.postLogin(
                    platform = platform,
                    authToken = authToken,
                )

                accountLocalDataSource.updateAccessToken(account.token.accessToken)
                accountLocalDataSource.updateRefreshToken(account.token.refreshToken)
                isRegisterUser = account.isRegister
            }

        suspend fun deleteAccount(reason: String): Result<Unit> =
            runCatching {
                accountRemoteDataSource.postWithdraw(reason = reason)
                accountLocalDataSource.deleteTokens()
            }

        suspend fun createTokens(): Result<Unit> =
            runCatching {
                val tokens = accountRemoteDataSource.postReissue(refreshToken = refreshToken())

                accountLocalDataSource.updateAccessToken(tokens.accessToken)
                accountLocalDataSource.updateRefreshToken(tokens.refreshToken)
            }

        suspend fun deleteTokens(deviceIdentifier: String): Result<Unit> =
            runCatching {
                accountRemoteDataSource
                    .postLogout(
                        refreshToken = refreshToken(),
                        deviceIdentifier = deviceIdentifier,
                    )

                accountLocalDataSource.deleteTokens()
            }
    }
