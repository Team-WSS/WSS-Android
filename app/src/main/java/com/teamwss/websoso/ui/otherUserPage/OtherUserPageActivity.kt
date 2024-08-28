package com.teamwss.websoso.ui.otherUserPage

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityOtherUserPageBinding
import com.teamwss.websoso.ui.otherUserPage.adapter.OtherUserPageViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserPageActivity :
    BaseActivity<ActivityOtherUserPageBinding>(R.layout.activity_other_user_page) {
    private val viewPagerAdapter: OtherUserPageViewPagerAdapter by lazy {
        OtherUserPageViewPagerAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewPager()
        setUpItemVisibilityOnToolBar()
    }

    private fun setUpViewPager() {
        val tabTitleItems = listOf(getText(R.string.other_user_page_library), getText(R.string.other_user_page_activity))
        binding.vpOtherUserPage.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tlOtherUserPage, binding.vpOtherUserPage) { tab, position ->
            tab.text = tabTitleItems[position]
        }.attach()
    }

    private fun setUpItemVisibilityOnToolBar() {
        binding.ablOtherUserPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarUi(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarUi(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) R.color.white else R.color.transparent
            tbOtherUserPage.setBackgroundColor(
                ContextCompat.getColor(
                    this@OtherUserPageActivity,
                    color
                )
            )
            tvOtherUserPageStickyTitle.isVisible = isCollapsed
            clOtherUserPageUserProfile.isVisible = !isCollapsed
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0f
    }
}