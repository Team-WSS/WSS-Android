package com.into.websoso.data.authenticator

import com.into.websoso.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebsosoAuthenticator
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (response.request.header("Authorization") == null) {
                return null
            }

            if (response.code == 401) {
                val newAccessToken = runCatching {
                    runBlocking {
                        authRepository.reissueToken()
                    }
                }.onFailure {
                    runBlocking {
                        authRepository.clearTokens()
                }
            }.getOrThrow()

            return response.request
                .newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
        return null
    }
}
