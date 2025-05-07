package com.into.websoso.core.network.datasource.account

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Singleton

internal interface AccountApi {
    @POST("auth/login/kakao")
    suspend fun postLoginWithKakao(
        @Header("Kakao-Access-Token") accessToken: String,
    ): KakaoLoginResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object AccountApiModule {
    @Provides
    @Singleton
    fun provideAccountApi(retrofit: Retrofit): AccountApi = retrofit.create(AccountApi::class.java)
}
