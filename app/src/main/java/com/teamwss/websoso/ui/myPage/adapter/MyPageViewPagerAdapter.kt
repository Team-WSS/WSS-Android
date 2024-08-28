package com.teamwss.websoso.ui.myPage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamwss.websoso.ui.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.myPage.myLibrary.MyLibraryFragment

class MyPageViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    val fragments = listOf(MyLibraryFragment(), MyActivityFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
