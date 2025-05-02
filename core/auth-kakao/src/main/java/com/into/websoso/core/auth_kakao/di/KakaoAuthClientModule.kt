package com.into.websoso.core.auth_kakao.di

import com.into.websoso.core.auth.AuthClient
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthPlatformKey
import com.into.websoso.core.auth_kakao.KakaoAuthClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
internal interface KakaoAuthClientModule {
    @Binds
    @IntoMap
    @AuthPlatformKey(AuthPlatform.KAKAO)
    fun bindKakaoAuthClient(kakaoAuthClient: KakaoAuthClient): AuthClient
}
