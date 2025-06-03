package com.into.websoso.core.common.util.navigator

import android.content.Context
import android.content.Intent
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.ui.login.LoginActivity
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.onboarding.OnboardingActivity
import com.into.websoso.ui.userStorage.UserStorageActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class WebsosoNavigator
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : NavigatorProvider {
        override fun navigateToLoginActivity(startActivity: (Intent) -> Unit) {
            val intent = LoginActivity.getIntent(context)
            startActivity(intent)
        }

        override fun navigateToMainActivity(startActivity: (Intent) -> Unit) {
            val intent = MainActivity.getIntent(context, true)
            startActivity(intent)
        }

        override fun navigateToOnboardingActivity(startActivity: (Intent) -> Unit) {
            val intent = OnboardingActivity.getIntent(context)
            startActivity(intent)
        }

        override fun navigateToUserStorageActivity(startActivity: (Intent) -> Unit) {
            val intent = UserStorageActivity.getIntent(context)
            startActivity(intent)
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface NavigatorModule {
    @Binds
    @Singleton
    fun bindWebsosoNavigator(websosoNavigator: WebsosoNavigator): NavigatorProvider
}
