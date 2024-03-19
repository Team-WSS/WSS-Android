package com.teamwss.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}