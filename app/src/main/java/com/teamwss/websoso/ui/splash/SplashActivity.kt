package com.teamwss.websoso.ui.splash

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityLoginBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(1000L)
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        startActivity(LoginActivity.from(this))
        finish()
    }
}