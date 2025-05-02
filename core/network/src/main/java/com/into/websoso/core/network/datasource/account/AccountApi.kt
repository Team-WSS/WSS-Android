package com.into.websoso.core.network.datasource.account

import retrofit2.http.Header
import retrofit2.http.POST

internal interface AccountApi {
    @POST("auth/login/kakao")
    suspend fun postLoginWithKakao(
        @Header("Kakao-Access-Token") accessToken: String,
    ): KakaoLoginResponseDto
}
