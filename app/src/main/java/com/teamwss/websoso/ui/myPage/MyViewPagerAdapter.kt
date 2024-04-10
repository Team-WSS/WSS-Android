package com.teamwss.websoso.ui.myPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.myPage.myfeed.MyFeedFragment
import com.teamwss.websoso.ui.myPage.mylibrary.MyLibraryFragment

class MyViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = listOf(MyLibraryFragment(), MyFeedFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}