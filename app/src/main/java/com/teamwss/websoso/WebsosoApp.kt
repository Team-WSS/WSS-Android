package com.teamwss.websoso

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.teamwss.websoso.data.FakeApi
import com.teamwss.websoso.data.repository.DefaultFeedRepository
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.domain.usecase.GetCategoryByUserGenderUseCase
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase

class WebsosoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    companion object {

        fun getUserRepository(): FakeUserRepository = FakeUserRepository()

        fun getFeedsUseCase(): GetFeedsUseCase = GetFeedsUseCase(DefaultFeedRepository(FakeApi))

        fun getCategoryByUserGenderUseCase(): GetCategoryByUserGenderUseCase =
            GetCategoryByUserGenderUseCase(getUserRepository())
    }
}
