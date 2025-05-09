package com.into.websoso.core.network.authenticator

import com.into.websoso.core.common.navigator.WebsosoNavigatorProvider
import com.into.websoso.data.account.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthorizationAuthenticator
    @Inject
    constructor(
        private val accountRepository: Provider<AccountRepository>,
        private val navigator: WebsosoNavigatorProvider,
    ) : Authenticator {
        private val mutex = Mutex()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (response.request.header("Authorization").isNullOrBlank() ||
                responseCount(response) >= MAX_ATTEMPT_COUNT
            ) {
                return null
            }

            return runBlocking(Dispatchers.IO) {
                mutex.withLock {
                    val updatedAccessToken = accountRepository.get().accessToken()
                    if (updatedAccessToken.isNotBlank()) {
                        return@runBlocking response.request
                            .newBuilder()
                            .header("Authorization", "Bearer $updatedAccessToken")
                            .build()
                    }

                    val refreshToken = accountRepository.get().refreshToken()
                    if (refreshToken.isBlank()) {
                        navigator.navigateToLoginActivity()
                        return@runBlocking null
                    }

                    return@runBlocking runCatching {
                        accountRepository.get().renewToken()
                        val newAccessToken = accountRepository.get().accessToken()
                        response.request
                            .newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    }.getOrElse {
                        navigator.navigateToLoginActivity()
                        null
                    }
                }
            }
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var current = response.priorResponse
            while (current != null) {
                count++
                current = current.priorResponse
            }
            return count
        }

        private fun responseCount2(response: Response): Int = generateSequence(response) { it.priorResponse }.count()

        companion object {
            private const val MAX_ATTEMPT_COUNT = 2
        }
    }
