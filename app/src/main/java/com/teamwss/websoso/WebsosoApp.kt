package com.teamwss.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.kakao.sdk.common.KakaoSdk
import com.teamwss.websoso.BuildConfig.KAKAO_APP_KEY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        KakaoSdk.init(this, KAKAO_APP_KEY)
    }
}
