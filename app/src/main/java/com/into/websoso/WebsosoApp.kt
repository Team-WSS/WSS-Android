package com.into.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.into.websoso.BuildConfig.KAKAO_APP_KEY
import com.into.websoso.data.authenticator.AuthFailureHandler
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.ui.login.LoginActivity
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WebsosoApp :
    Application(),
    AuthFailureHandler {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate() {
        super.onCreate()
        authRepository.authFailureHandler = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        KakaoSdk.init(this, KAKAO_APP_KEY)
    }

    override fun onAuthFailed() {
        UserApiClient.instance.logout {
            this.startActivity(LoginActivity.getIntent(this))
        }
    }
}
