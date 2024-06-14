package com.teamwss.websoso.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityLoginBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.login.adapter.ImageViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewPager()
    }

    private fun setupViewPager() {
        viewModel.images.observe(this) { images ->
            if (images != null) {
                binding.vpLogin.adapter = ImageViewPagerAdapter(images)
                setupDotsIndicator()
            }
        }
    }

    private fun setupDotsIndicator() {
        binding.dotsIndicatorLogin.attachTo(binding.vpLogin)
    }

    companion object{

        fun from(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
            }
        }
    }
}