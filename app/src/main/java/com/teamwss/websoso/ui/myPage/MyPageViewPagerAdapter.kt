package com.teamwss.websoso.ui.myPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.myPage.myLibrary.MyLibraryFragment

class MyPageViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    val fragments = listOf(MyLibraryFragment(), MyActivityFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragments.first()
            else -> fragments.last()
        }
    }
}