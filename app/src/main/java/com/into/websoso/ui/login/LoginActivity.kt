package com.into.websoso.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.custom.WebsosoCustomToast
import com.into.websoso.data.remote.api.OAuthService
import com.into.websoso.databinding.ActivityLoginBinding
import com.into.websoso.ui.login.adapter.ImageViewPagerAdapter
import com.into.websoso.ui.login.model.LoginUiState
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
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
                is LoginUiState.Loading -> {
                    binding.wllLogin.visibility = View.VISIBLE
                }

                is LoginUiState.Success -> {
                    binding.wllLogin.visibility = View.INVISIBLE

                    when (state.isRegistered) {
                        true -> {
                            startActivity(MainActivity.getIntent(this@LoginActivity, true))
                        }

                        false -> {
                            startActivity(
                                OnboardingActivity.getIntent(
                                    this@LoginActivity,
                                    viewModel.accessToken,
                                    viewModel.refreshToken,
                                )
                            )
                        }
                    }
                    finish()
                }

                is LoginUiState.Failure -> {
                    binding.wllLogin.visibility = View.INVISIBLE
                    firebaseAnalytics.logEvent("login_failure") {
                        param("error_message", state.error.message ?: "Unknown Error")
                        param("timestamp", System.currentTimeMillis().toString())
                    }
                }

                is LoginUiState.Idle -> {
                    binding.wllLogin.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setupDotsIndicator() {
        binding.dotsIndicatorLogin.attachTo(binding.vpLogin)
    }

    private fun onWithoutLoginButtonClick() {
        binding.tvLoginWithoutLogin.setOnClickListener {
            startActivity(MainActivity.getIntent(this, false))
        }
    }

    private fun onKakaoLoginButtonClick() {
        binding.ivLoginKakao.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    kakaoAuthService.login()
                }.onSuccess { token ->
                    viewModel.loginWithKakao(token.accessToken)
                }.onFailure {
                    WebsosoCustomToast.make(this@LoginActivity)
                        .setText("카카오톡 소셜 로그인에 실패했어요")
                        .setIcon(R.drawable.ic_novel_rating_alert)
                        .show()
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

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}
