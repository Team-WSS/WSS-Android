package com.into.websoso.core.network.interceptor

import com.into.websoso.data.account.AccountTokenProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthorizationInterceptor
    @Inject
    constructor(
        private val accountToken: AccountTokenProvider,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            if (isSkippedPath(request)) return chain.proceed(request)

            val newRequest = request
                .newBuilder()
                .addHeader("Authorization", "Bearer $accountToken")
                .build()

            return chain.proceed(newRequest)
        }

        private fun isSkippedPath(request: Request): Boolean =
            EXCLUDED_PATHS.any { path ->
                request.url.encodedPath.contains(path)
            }

        companion object {
            private val EXCLUDED_PATHS = listOf(
                "auth/login/kakao",
                "reissue",
                "minimum-version",
            )
        }
    }
