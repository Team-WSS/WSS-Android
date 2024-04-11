package com.teamwss.websoso.ui.novelDetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.novelDetail.fragment.NovelFeedFragment
import com.teamwss.websoso.ui.novelDetail.fragment.NovelInfoFragment

class NovelDetailPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = NOVEL_DETAIL_TAB_COUNT

    override fun createFragment(position: Int):Fragment {
        return when (position) {
            INFO_FRAGMENT_PAGE -> NovelInfoFragment()
            FEED_FRAGMENT_PAGE -> NovelFeedFragment()
            else -> {NovelInfoFragment()}
        }
    }

    companion object {
        const val INFO_FRAGMENT_PAGE = 0
        const val FEED_FRAGMENT_PAGE = 1
        const val NOVEL_DETAIL_TAB_COUNT = 2
    }
}