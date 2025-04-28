package com.into.websoso.ui.otherUserPage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.into.websoso.ui.otherUserPage.otherUserActivity.OtherUserActivityFragment
import com.into.websoso.ui.otherUserPage.otherUserLibrary.OtherUserLibraryFragment

class OtherUserPageViewPagerAdapter(
    activity: FragmentActivity,
    private val userId: Long,
) : FragmentStateAdapter(activity) {
    private val fragments = listOf(OtherUserLibraryFragment(), OtherUserActivityFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> OtherUserLibraryFragment.newInstance(userId)
            1 -> OtherUserActivityFragment.newInstance(userId)
            else -> throw IllegalArgumentException("Invalid position")
        }
}
