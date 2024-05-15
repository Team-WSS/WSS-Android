package com.teamwss.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.data.repository.FakeSosoPickRepository
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {

        fun getUserRepository(): FakeUserRepository = FakeUserRepository()

        fun getFeedsUseCase(): GetFeedsUseCase = GetFeedsUseCase(FakeFeedRepository())

        fun getSosoPickRepository(): FakeSosoPickRepository = FakeSosoPickRepository()
    }
}
