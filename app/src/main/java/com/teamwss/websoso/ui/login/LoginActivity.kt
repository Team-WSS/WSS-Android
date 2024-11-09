package com.teamwss.websoso.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.data.remote.service.OAuthService
import com.teamwss.websoso.databinding.ActivityLoginBinding
import com.teamwss.websoso.ui.login.adapter.ImageViewPagerAdapter
import com.teamwss.websoso.ui.login.model.LoginUiState
import com.teamwss.websoso.ui.main.MainActivity
import com.teamwss.websoso.ui.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val viewModel: LoginViewModel by viewModels()
    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    @Inject
    lateinit var kakaoAuthService: OAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupObserver()
        onWithoutLoginButtonClick()
        onKakaoLoginButtonClick()
        startAutoScroll()
        viewModel.autoLogin()
    }

    private fun setupObserver() {
        viewModel.loginImages.observe(this) { images ->
            if (images != null) {
                binding.vpLogin.adapter = ImageViewPagerAdapter(images)
                setupDotsIndicator()
            }
        }

        viewModel.loginUiState.observe(this) { state ->
            when (state) {
                is LoginUiState.Success -> {
                    if (state.isRegistered) {
                        startActivity(MainActivity.getIntent(this@LoginActivity))
                    } else {
                        startActivity(
                            OnboardingActivity.getIntent(this@LoginActivity).apply {
                                putExtra(ACCESS_TOKEN_KEY, state.accessToken)
                                putExtra(REFRESH_TOKEN_KEY, state.refreshToken)
                            }
                        )
                    }
                    finish()
                }

                is LoginUiState.Failure -> {
                    Toast.makeText(this, "로그인 실패: ${state.error.message}", Toast.LENGTH_SHORT)
                        .show()
                }

                is LoginUiState.Idle -> {}
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

    private fun onKakaoLoginButtonClick() {
        binding.ivLoginKakao.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    kakaoAuthService.login()
                }.onSuccess { token ->
                    viewModel.loginWithKakao(token.accessToken)
                }.onFailure { error ->
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 실패: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startAutoScroll() {
        val pageCount = viewModel.loginImages.value?.size ?: 0

        runnable = Runnable {
            if (pageCount > 0) {
                if (currentPage < pageCount - 1) {
                    currentPage++
                    binding.vpLogin.setCurrentItem(currentPage, true)
                    handler.postDelayed(runnable, PAGE_SCROLL_DELAY)
                } else {
                    handler.removeCallbacks(runnable)
                }
            }
        }
        handler.postDelayed(runnable, PAGE_SCROLL_DELAY)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    companion object {
        private const val PAGE_SCROLL_DELAY = 2000L
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
