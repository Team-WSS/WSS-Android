package com.teamwss.websoso.ui.createFeed

import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R.color.bg_detail_explore_chip_background_selector
import com.teamwss.websoso.R.color.bg_detail_explore_chip_stroke_selector
import com.teamwss.websoso.R.color.bg_detail_explore_chip_text_selector
import com.teamwss.websoso.R.color.gray_200_AEADB3
import com.teamwss.websoso.R.layout
import com.teamwss.websoso.R.string.wset_create_feed_search_novel
import com.teamwss.websoso.R.style.body2
import com.teamwss.websoso.R.style.body4
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.databinding.ActivityCreateFeedBinding
import com.teamwss.websoso.ui.createFeed.model.CreateFeedCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFeedActivity : BaseActivity<ActivityCreateFeedBinding>(layout.activity_create_feed) {
    private val createFeedViewModel: CreateFeedViewModel by viewModels()
    private lateinit var searchNovelDialog: CreateFeedSearchNovelBottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCategoryChips()

        setupView()
        bindViewModel()
        setupObserver()
    }

    private fun setupView() {
        binding.wsetCreateFeedSearchNovel.apply {
            setWebsosoFocusableInTouchMode(false)
            setWebsosoSearchHint(getString(wset_create_feed_search_novel))
            setWebsosoSearchHintTextColor(gray_200_AEADB3)
            setWebsosoSearchTextAppearance(body4)
            setWebsosoOnClickListener { showSearchNovelDialog() }
        }
    }

    private fun showSearchNovelDialog() {
        CreateFeedSearchNovelBottomSheetDialog.newInstance().also {
            it.show(
                supportFragmentManager,
                CreateFeedSearchNovelBottomSheetDialog.CREATE_FEED_SEARCH_NOVEL_TAG,
            )
        }
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
                setWebsosoChipTextAppearance(body2)
                setWebsosoChipTextColor(bg_detail_explore_chip_text_selector)
                setWebsosoChipStrokeColor(bg_detail_explore_chip_stroke_selector)
                setWebsosoChipBackgroundColor(bg_detail_explore_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
                setWebsosoChipPaddingHorizontal(6.7f.toFloatScaledByPx())
                setWebsosoChipRadius(20f.toFloatScaledByPx())
                setOnWebsosoChipClick { createFeedViewModel.updateSelectedCategory(category.ordinal) }
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }
}
