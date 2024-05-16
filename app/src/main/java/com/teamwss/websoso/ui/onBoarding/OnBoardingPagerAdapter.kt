package com.teamwss.websoso.ui.onBoarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.onBoarding.OnBoardingActivity
import com.teamwss.websoso.ui.onBoarding.first.OnBoardingFirstFragment
import com.teamwss.websoso.ui.onBoarding.second.OnBoardingSecondFragment
import com.teamwss.websoso.ui.onBoarding.third.OnBoardingThirdFragment

class OnBoardingPagerAdapter(activity: OnBoardingActivity) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingFirstFragment()
            1 -> OnBoardingSecondFragment()
            2 -> OnBoardingThirdFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}