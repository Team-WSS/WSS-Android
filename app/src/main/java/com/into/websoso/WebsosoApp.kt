package com.into.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.into.websoso.BuildConfig.KAKAO_APP_KEY
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        KakaoSdk.init(this, KAKAO_APP_KEY)
    }
}
