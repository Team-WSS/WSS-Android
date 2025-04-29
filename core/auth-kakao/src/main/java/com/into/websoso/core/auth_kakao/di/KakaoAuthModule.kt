package com.into.websoso.core.auth_kakao.di

import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.KakaoAuth
import com.into.websoso.core.auth_kakao.KakaoAuthClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
internal interface AuthModule {
    @Binds
    @KakaoAuth
    fun bindKakaoAuthClient(kakaoAuthClient: KakaoAuthClient): AuthClient
}
