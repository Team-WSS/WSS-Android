package com.teamwss.websoso.ui.onBoarding

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityOnBoardingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.onBoarding.adapter.OnBoardingPagerAdapter

class OnBoardingActivity :
    BindingActivity<ActivityOnBoardingBinding>(R.layout.activity_on_boarding) {
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupViewPager()
        observePageChange()
        observeProgressBarChanges()
    }

    private fun setupViewPager() {
        binding.vpOnBoarding.adapter = OnBoardingPagerAdapter(this)
    }

    private fun observePageChange() {
        binding.vpOnBoarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.updateCurrentPage(position)
            }
        })
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
        ).apply {
            duration = ANIMATION_DURATION_TIME
            start()
        }
    }

    companion object {
        const val ANIMATION_PROPERTY_NAME = "progress"
        const val ANIMATION_DURATION_TIME = 200L
    }
}