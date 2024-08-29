package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.databinding.ActivityCreateFeedBinding
import com.teamwss.websoso.ui.createFeed.model.CreateFeedCategory

class CreateFeedActivity : BaseActivity<ActivityCreateFeedBinding>(R.layout.activity_create_feed) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCategoryChips()
    }

    private fun setupCategoryChips() {
        CreateFeedCategory.entries.forEach { category ->
            WebsosoChip(this).apply {
                setWebsosoChipText(category.titleKr)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_detail_explore_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_detail_explore_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_detail_explore_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
                setWebsosoChipPaddingHorizontal(8f.toFloatScaledByPx())
                setWebsosoChipRadius(20f.toFloatScaledByPx())
                setOnWebsosoChipClick {}
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }
}
