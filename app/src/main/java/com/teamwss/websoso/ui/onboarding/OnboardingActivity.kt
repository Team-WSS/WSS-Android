package com.teamwss.websoso.ui.onboarding

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityOnboardingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.onboarding.model.OnboardingPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity :
    BindingActivity<ActivityOnboardingBinding>(R.layout.activity_onboarding) {
    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupViewPager()
        observeCurrentPageChanges()
        observeProgressBarChanges()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setupViewPager() {
        binding.vpOnboarding.adapter = OnboardingPagerAdapter(this)
    }

    private fun observeCurrentPageChanges() {
        viewModel.currentPage.observe(this) { page ->
            val pageIndex = OnboardingPage.pages.indexOf(page)
            if (binding.vpOnboarding.currentItem != pageIndex) {
                binding.vpOnboarding.setCurrentItem(pageIndex, true)
            }
        }
    }

    private fun observeProgressBarChanges() {
        viewModel.progressBarPercent.observe(this) { percent ->
            animateProgressBar(percent)
        }
    }

    private fun animateProgressBar(targetProgress: Int) {
        ObjectAnimator.ofInt(
            binding.pbOnboarding,
            ANIMATION_PROPERTY_NAME,
            binding.pbOnboarding.progress,
            targetProgress
        ).run {
            duration = ANIMATION_DURATION_TIME
            start()
        }
    }

    companion object {
        private const val ANIMATION_PROPERTY_NAME = "progress"
        private const val ANIMATION_DURATION_TIME = 200L
    }
}
