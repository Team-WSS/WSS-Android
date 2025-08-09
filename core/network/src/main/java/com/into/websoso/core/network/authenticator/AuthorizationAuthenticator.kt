package com.into.websoso.core.network.authenticator

import com.into.websoso.core.auth.AuthSessionManager
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.common.extensions.ThrottleHelper
import com.into.websoso.data.account.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
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
internal class AuthorizationAuthenticator
    @Inject
    constructor(
        private val accountRepository: Provider<AccountRepository>,
        private val sessionManager: AuthSessionManager,
        private val throttle: ThrottleHelper,
        @Dispatcher(WebsosoDispatchers.IO) private val dispatcher: CoroutineDispatcher,
    ) : Authenticator {
        private val mutex: Mutex = Mutex()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (shouldSkipCondition(response)) return null

            val renewedToken = runBlocking(dispatcher) {
                mutex.withLock {
                    when (response.isRefreshNeeded()) {
                        true -> throttle { renewToken() }
                        false -> accountRepository.get().accessToken()
                    }
                }
            } ?: return null

            return response.request
                .newBuilder()
                .header("Authorization", "Bearer $renewedToken")
                .build()
        }

        private fun shouldSkipCondition(response: Response): Boolean =
            response.request.header("Authorization").isNullOrBlank() ||
                response.retryAttemptCount() >= MAX_ATTEMPT_COUNT

        private fun Response.retryAttemptCount(): Int =
            generateSequence(this) {
                it.priorResponse
            }.count()

        private suspend fun Response.isRefreshNeeded(): Boolean {
            val updatedAccessToken = accountRepository.get().accessToken()
            val oldAccessToken = request
                .header("Authorization")
                ?.removePrefix("Bearer ")

            return oldAccessToken == updatedAccessToken
        }

        private suspend fun renewToken(): String? =
            accountRepository.get().createTokens().fold(
                onSuccess = { accountRepository.get().accessToken() },
                onFailure = {
                    sessionManager.onSessionExpired()
                    null
                },
            )

        companion object {
            private const val MAX_ATTEMPT_COUNT = 2
        }
    }
