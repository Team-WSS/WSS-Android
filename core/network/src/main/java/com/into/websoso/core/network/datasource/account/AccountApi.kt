package com.into.websoso.core.network.datasource.account

import com.into.websoso.core.network.datasource.account.model.response.KakaoLoginResponseDto
import com.into.websoso.core.network.datasource.account.model.request.KakaoLogoutRequestDto
import com.into.websoso.core.network.datasource.account.model.request.TokenReissueRequestDto
import com.into.websoso.core.network.datasource.account.model.response.TokenReissueResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Singleton

internal interface AccountApi {
    @POST("auth/login/kakao")
    suspend fun postLoginWithKakao(
        @Header("Kakao-Access-Token") accessToken: String,
    ): KakaoLoginResponseDto

    @POST("auth/logout")
    suspend fun postLogoutWithKakao(
        @Body kakaoLogoutRequestDto: KakaoLogoutRequestDto,
    )

    @POST("reissue")
    suspend fun postReissueToken(
        @Body tokenReissueRequestDto: TokenReissueRequestDto,
    ): TokenReissueResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object AccountApiModule {
    @Provides
    @Singleton
    internal fun provideAccountApi(retrofit: Retrofit): AccountApi = retrofit.create(AccountApi::class.java)
}
