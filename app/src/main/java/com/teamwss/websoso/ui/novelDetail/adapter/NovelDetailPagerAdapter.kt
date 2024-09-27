package com.teamwss.websoso.ui.novelDetail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.novelFeed.NovelFeedFragment
import com.teamwss.websoso.ui.novelInfo.NovelInfoFragment

class NovelDetailPagerAdapter(
    fragmentActivity: FragmentActivity,
    novelId: Long,
    isLogin: Boolean,
) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments =
        arrayOf(NovelInfoFragment.newInstance(novelId), NovelFeedFragment.newInstance(novelId, isLogin))

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
