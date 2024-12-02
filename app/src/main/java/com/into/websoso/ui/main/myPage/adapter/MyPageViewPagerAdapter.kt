package com.into.websoso.ui.main.myPage.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.into.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.into.websoso.ui.main.myPage.myLibrary.MyLibraryFragment

class MyPageViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    val fragments = listOf(MyLibraryFragment(), MyActivityFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
