package com.teamwss.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase

class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {
        
        fun getFeedsUseCase(): GetFeedsUseCase = GetFeedsUseCase(FakeFeedRepository())
    }
}