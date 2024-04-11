package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter

class NovelDetailActivity :
    BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        binding.vpNovelDetail.adapter = NovelDetailPagerAdapter(this)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tlNovelDetail, binding.vpNovelDetail) { tab, position ->
            tab.text = when (position) {
                INFO_FRAGMENT_PAGE -> getString(R.string.novel_detail_info)
                FEED_FRAGMENT_PAGE -> getString(R.string.novel_detail_feed)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }

    companion object {
        const val INFO_FRAGMENT_PAGE = 0
        const val FEED_FRAGMENT_PAGE = 1
    }
}