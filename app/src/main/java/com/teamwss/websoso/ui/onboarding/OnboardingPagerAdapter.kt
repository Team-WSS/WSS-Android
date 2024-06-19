package com.teamwss.websoso.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.onboarding.first.OnboardingFirstFragment
import com.teamwss.websoso.ui.onboarding.second.OnboardingSecondFragment
import com.teamwss.websoso.ui.onboarding.third.OnboardingThirdFragment

class OnboardingPagerAdapter(activity: OnboardingActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFirstFragment()
            1 -> OnboardingSecondFragment()
            2 -> OnboardingThirdFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}