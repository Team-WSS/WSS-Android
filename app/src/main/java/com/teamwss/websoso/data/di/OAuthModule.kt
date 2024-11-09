package com.teamwss.websoso.data.di

import com.kakao.sdk.user.UserApiClient
import com.teamwss.websoso.data.remote.service.KakaoAuthService
import com.teamwss.websoso.data.remote.service.OAuthService
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
