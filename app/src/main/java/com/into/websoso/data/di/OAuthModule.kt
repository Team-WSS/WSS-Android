package com.into.websoso.data.di

import com.into.websoso.data.remote.api.KakaoAuthService
import com.into.websoso.data.remote.api.OAuthService
import com.kakao.sdk.user.UserApiClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object OAuthModule {
    @Provides
    fun provideKakaoApiClient(): UserApiClient = UserApiClient.instance

    @Module
    @InstallIn(ActivityComponent::class)
    interface Binder {
        @Binds
        fun bindKakaoAuthService(service: KakaoAuthService): OAuthService
    }
}
