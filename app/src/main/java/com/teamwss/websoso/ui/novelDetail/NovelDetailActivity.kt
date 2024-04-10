package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter

class NovelDetailActivity: BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vpNovelDetail.adapter = NovelDetailPagerAdapter(this)
        TabLayoutMediator(binding.tlNovelDetail, binding.vpNovelDetail) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.ti_novel_detail_info)
                1 -> getString(R.string.ti_novel_detail_feed)
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()
    }
}