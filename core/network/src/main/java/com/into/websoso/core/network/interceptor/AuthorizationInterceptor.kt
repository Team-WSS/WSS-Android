package com.into.websoso.core.network.interceptor

import android.util.Log
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.data.account.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
internal class AuthorizationInterceptor
    @Inject
    constructor(
        private val accountRepository: Provider<AccountRepository>,
        @Dispatcher(WebsosoDispatchers.IO) private val dispatcher: CoroutineDispatcher,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.d("123123", "인터셉터: ${request.url}")
            if (shouldSkipCondition(request)) return chain.proceed(request)
            Log.d("123123", "인터셉터: 뚫림")
            val token = runBlocking(dispatcher) { accountRepository.get().accessToken() }

            if (token.isBlank()) return chain.proceed(request)

            val newRequest = request
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            return chain.proceed(newRequest)
        }

        private fun shouldSkipCondition(request: Request): Boolean =
            EXCLUDED_PATHS.any { path ->
                Log.d("123123", request.url.toString())
                request.url.encodedPath.endsWith("/$path")
            }

        companion object {
            private val EXCLUDED_PATHS = listOf(
                "auth/login/kakao",
                "reissue",
                "minimum-version",
            )
        }
    }
