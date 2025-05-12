package com.into.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.into.websoso.BuildConfig.KAKAO_APP_KEY
import com.into.websoso.core.auth.AuthSessionManager
import com.into.websoso.core.auth.SessionState
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.util.collectWithLifecycle
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WebsosoApp : Application() {
    @Inject
    lateinit var sessionManager: AuthSessionManager

    @Inject
    lateinit var navigatorProvider: NavigatorProvider

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        subscribeSessionState()
        KakaoSdk.init(this, KAKAO_APP_KEY)
    }

    private fun subscribeSessionState() {
        sessionManager.sessionState.collectWithLifecycle(ProcessLifecycleOwner.get()) { state ->
            if (state is SessionState.Expired) {
                sessionManager.clearSessionState()
                navigatorProvider.navigateToLoginActivity()
            }
        }
    }
}
