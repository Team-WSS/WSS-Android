package com.teamwss.websoso.ui.otherUserPage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.otherUserPage.otherUserActivity.OtherUserActivityFragment
import com.teamwss.websoso.ui.otherUserPage.otherUserLibrary.OtherUserLibraryFragment

class OtherUserPageViewPagerAdapter(activity: FragmentActivity) :
    FragmentStateAdapter(activity) {

    private val fragments = listOf(OtherUserLibraryFragment(), OtherUserActivityFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}