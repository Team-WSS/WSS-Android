package com.teamwss.websoso.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityLoginBinding
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.ui.login.adapter.ImageViewPagerAdapter
import com.teamwss.websoso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObserver()
        onWithoutLoginButtonClick()
    }

    private fun setupObserver() {
        viewModel.loginImages.observe(this) { images ->
            if (images != null) {
                binding.vpLogin.adapter = ImageViewPagerAdapter(images)
                setupDotsIndicator()
            }
        }
    }

    private fun setupDotsIndicator() {
        binding.dotsIndicatorLogin.attachTo(binding.vpLogin)
    }

    private fun onWithoutLoginButtonClick() {
        binding.tvLoginWithoutLogin.setOnClickListener {
            startActivity(MainActivity.getIntent(this))
        }
    }

    companion object {

        fun from(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
            }
        }
    }
}