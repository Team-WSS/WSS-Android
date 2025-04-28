package com.into.websoso.data.interceptor

import com.into.websoso.data.repository.AuthRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer ${authRepository.accessToken}")
                .build()

            return chain.proceed(request)
        }
    }
