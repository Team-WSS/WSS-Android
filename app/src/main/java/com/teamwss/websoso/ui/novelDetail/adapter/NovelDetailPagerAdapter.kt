package com.teamwss.websoso.ui.novelDetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.novelDetail.fragment.NovelFeedFragment
import com.teamwss.websoso.ui.novelDetail.fragment.NovelInfoFragment

class NovelDetailPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = arrayOf(NovelInfoFragment(), NovelFeedFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    companion object {
        const val INFO_FRAGMENT_PAGE = 0
        const val FEED_FRAGMENT_PAGE = 1
    }
}
