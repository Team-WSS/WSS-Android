package com.teamwss.websoso.ui.onBoarding

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityOnBoardingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity

class OnBoardingActivity :
    BindingActivity<ActivityOnBoardingBinding>(R.layout.activity_on_boarding) {
    private val viewModel: OnBoardingViewModel by viewModels { OnBoardingViewModel.Factory }

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
        binding.vpOnBoarding.adapter = OnBoardingPagerAdapter(this)
    }


    private fun observeCurrentPageChanges() {
        viewModel.currentPage.observe(this) { page ->
            if (binding.vpOnBoarding.currentItem != page.ordinal) {
                binding.vpOnBoarding.setCurrentItem(page.ordinal, true)
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
            binding.pbOnBoarding,
            ANIMATION_PROPERTY_NAME,
            binding.pbOnBoarding.progress,
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
