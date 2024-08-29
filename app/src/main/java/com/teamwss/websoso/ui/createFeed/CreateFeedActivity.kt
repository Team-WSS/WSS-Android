package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.databinding.ActivityCreateFeedBinding
import com.teamwss.websoso.ui.createFeed.model.CreateFeedCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFeedActivity : BaseActivity<ActivityCreateFeedBinding>(R.layout.activity_create_feed) {
    private val createFeedViewModel: CreateFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCategoryChips()

        bindViewModel()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = createFeedViewModel
    }

    private fun setupObserver() {
        createFeedViewModel.isActivated.observe(this) { isSelected ->
            binding.tvCreateFeedDoneButton.isSelected = isSelected
        }
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
                setWebsosoChipPaddingHorizontal(6.7f.toFloatScaledByPx())
                setWebsosoChipRadius(20f.toFloatScaledByPx())
                setOnWebsosoChipClick { createFeedViewModel.updateSelectedCategory(category.ordinal) }
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }
}
