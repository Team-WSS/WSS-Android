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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_splash) {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObserver()
        splashViewModel.autoLogin()
    }

    private fun setupObserver() {
        splashViewModel.isAutoLogin.observe(this) { isAutoLogin ->
            lifecycleScope.launch {
                delay(1000L)
                splashViewModel.updateMinimumVersion { isUpdateRequired ->
                    if (isUpdateRequired) {
                        showMinimumVersionDialog()
                    } else {
                        when (isAutoLogin) {
                            true -> navigateToMainActivity()
                            false -> navigateToLoginActivity()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(MainActivity.getIntent(this, true))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(LoginActivity.getIntent(this))
        finish()
    }

    private fun showMinimumVersionDialog() {
        val dialog = MinimumVersionDialogFragment.newInstance()
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, MINIMUM_VERSION_TAG)
    }

    companion object {
        private const val MINIMUM_VERSION_TAG = "MinimumVersionDialog"
    }
}
