package com.into.websoso.ui.onboarding

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.ActivityOnboardingBinding
import com.into.websoso.ui.onboarding.model.OnboardingPage
import com.into.websoso.ui.onboarding.welcome.WelcomeActivity
import com.into.websoso.ui.termsAgreement.TermsAgreementDialogBottomSheet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private val viewModel: OnboardingViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupViewPager()
        setupObserver()
        onSkipGeneralButtonClick()
        showTermsAgreementDialog()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setupViewPager() {
        binding.vpOnboarding.adapter = OnboardingPagerAdapter(this)
    }

    private fun setupObserver() {
        viewModel.currentPage.observe(this) { page ->
            val pageIndex = OnboardingPage.pages.indexOf(page)
            if (binding.vpOnboarding.currentItem != pageIndex) {
                binding.vpOnboarding.setCurrentItem(pageIndex, true)
            }
        }

        viewModel.progressBarPercent.observe(this) { percent ->
            animateProgressBar(percent)
        }

        viewModel.isUserProfileSubmit.observe(this) { isUserProfileSubmit ->
            if (isUserProfileSubmit) {
                val nickname = viewModel.currentNicknameInput.value ?: "웹소소"
                startActivity(WelcomeActivity.getIntent(this, nickname))
                finish()
            }
        }
    }

    private fun animateProgressBar(targetProgress: Int) {
        ObjectAnimator
            .ofInt(
                binding.pbOnboarding,
                ANIMATION_PROPERTY_NAME,
                binding.pbOnboarding.progress,
                targetProgress,
            ).run {
                duration = ANIMATION_DURATION_TIME
                start()
            }
    }

    private fun onSkipGeneralButtonClick() {
        binding.tvOnboardingSkipGeneral.setOnClickListener {
            singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                viewModel.clearGenreSelection()
                viewModel.submitUserProfile()
            }
        }
    }

    private fun showTermsAgreementDialog() {
        TermsAgreementDialogBottomSheet.newInstance()
            .show(supportFragmentManager, "TermsAgreementDialog")
    }

    companion object {
        private const val ANIMATION_PROPERTY_NAME = "progress"
        private const val ANIMATION_DURATION_TIME = 200L
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"

        fun getIntent(context: Context): Intent = Intent(context, OnboardingActivity::class.java)

        fun getIntent(
            context: Context,
            accessToken: String,
            refreshToken: String,
        ): Intent =
            Intent(context, OnboardingActivity::class.java).apply {
                putExtra(ACCESS_TOKEN_KEY, accessToken)
                putExtra(REFRESH_TOKEN_KEY, refreshToken)
            }
    }
}
