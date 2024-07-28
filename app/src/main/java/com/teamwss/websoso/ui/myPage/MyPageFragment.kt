package com.teamwss.websoso.ui.myPage

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentMyPageBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BindingFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val viewPagerAdapter by lazy { MyPageViewPagerAdapter(requireActivity()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPager()
        setItemVisibilityOnToolBar()
    }

    private fun setUpViewPager() {
        val tabTitleItems =
            listOf(getText(R.string.my_page_library), getText(R.string.my_page_activity))
        binding.vpMyPage.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tlMyPage, binding.vpMyPage) { tab, position ->
            tab.text = tabTitleItems[position]
        }.attach()
    }

    private fun setItemVisibilityOnToolBar() {
        binding.ablMyPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarAppearance(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarAppearance(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) R.color.white else R.color.transparent
            tbMyPage.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    color
                )
            )

            tvMyPageStickyTitle.visibility =
                if (isCollapsed) View.VISIBLE else View.GONE

            val window = requireActivity().window
            window.statusBarColor = ContextCompat.getColor(
                requireContext(),
                if (isCollapsed) R.color.white else android.R.color.transparent
            )

            clMyPageUserProfile.visibility = if (isCollapsed) View.INVISIBLE else View.VISIBLE
        }
    }

    companion object {
        const val TOOLBAR_COLLAPSE_THRESHOLD = 0
    }

}