package com.into.websoso.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.databinding.ActivityLoginBinding
import com.into.websoso.ui.login.LoginActivity
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.splash.dialog.MinimumVersionDialogFragment
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_splash) {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObserver()
    }

    private fun setupObserver() {
        splashViewModel.isUpdateRequired.observe(this) { isUpdateRequired ->
            if (isUpdateRequired) {
                showMinimumVersionDialog()
                return@observe
            }
            splashViewModel.updateMyProfile()
        }

        splashViewModel.error.observe(this) { isError ->
            if (isError) {
                UserApiClient.instance.logout {
                    startActivity(LoginActivity.getIntent(this))
                }
            }
        }

        splashViewModel.isAutoLogin.observe(this) { isAutoLogin ->
            lifecycleScope.launch {
                delay(1000L)
                when (isAutoLogin) {
                    true -> navigateToMainActivity()
                    false -> navigateToLoginActivity()
                }
            }
        }
    }

    private fun showMinimumVersionDialog() {
        val dialog = MinimumVersionDialogFragment.newInstance()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, MINIMUM_VERSION_TAG)
    }

    private fun navigateToMainActivity() {
        startActivity(MainActivity.getIntent(this, true))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(LoginActivity.getIntent(this))
        finish()
    }

    companion object {
        private const val MINIMUM_VERSION_TAG = "MinimumVersionDialog"
    }
}
