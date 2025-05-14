package com.into.websoso.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure.ANDROID_ID
import android.provider.Settings.Secure.getString
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.R
import com.into.websoso.core.common.navigator.NavigatorProvider
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.ui.splash.UiEffect.NavigateToLogin
import com.into.websoso.ui.splash.UiEffect.NavigateToMain
import com.into.websoso.ui.splash.UiEffect.ShowDialog
import com.into.websoso.ui.splash.dialog.MinimumVersionDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity(R.layout.activity_splash) {
    @Inject
    lateinit var websosoNavigator: NavigatorProvider

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateUserDeviceIdentifier()
        collectUiEffect()
    }

    @SuppressLint("HardwareIds")
    private fun updateUserDeviceIdentifier() {
        val deviceId = getString(contentResolver, ANDROID_ID)
        splashViewModel.updateUserDeviceIdentifier(deviceIdentifier = deviceId)
    }

    private fun collectUiEffect() {
        splashViewModel.uiEffect.collectWithLifecycle(this) { uiEffect ->
            when (uiEffect) {
                NavigateToLogin -> websosoNavigator.navigateToLoginActivity()
                NavigateToMain -> websosoNavigator.navigateToMainActivity()
                ShowDialog -> showMinimumVersionDialog()
            }
        }
    }

    private fun showMinimumVersionDialog() {
        val dialog = MinimumVersionDialogFragment.newInstance()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, MinimumVersionDialogFragment.MINIMUM_VERSION_TAG)
    }
}
