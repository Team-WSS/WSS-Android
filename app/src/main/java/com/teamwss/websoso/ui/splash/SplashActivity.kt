package com.teamwss.websoso.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityLoginBinding
import com.teamwss.websoso.ui.login.LoginActivity
import com.teamwss.websoso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_splash) {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val autoLoginJob = launch { splashViewModel.autoLogin() }
            val delayJob = launch { delay(1000L) }

            joinAll(autoLoginJob, delayJob)

            when (splashViewModel.isAutoLoginSuccess.value) {
                true -> navigateToMainActivity()
                else -> navigateToLoginActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(MainActivity.getIntent(this))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(LoginActivity.getIntent(this))
        finish()
    }
}
