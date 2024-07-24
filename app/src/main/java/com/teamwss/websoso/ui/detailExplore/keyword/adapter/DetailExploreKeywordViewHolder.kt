package com.teamwss.websoso.ui.detailExplore.keyword.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel
import com.teamwss.websoso.util.toIntScaledByDp

class DetailExploreKeywordViewHolder(
    private val binding: ItemNovelRatingKeywordBinding,
    private val onKeywordClick: (
        keyword: DetailExploreKeywordModel.CategoryModel.KeywordModel,
        isClicked: Boolean,
    ) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private var _isChipSetting: Boolean = false
    val isChipSetting: Boolean get() = _isChipSetting

    init {
        binding.setupExpandToggleBtn()
    }

    private fun ItemNovelRatingKeywordBinding.setupExpandToggleBtn() {
        ivNovelRatingKeywordToggle.setOnClickListener {
            ivNovelRatingKeywordToggle.isSelected = !ivNovelRatingKeywordToggle.isSelected
            val layoutParams =
                wcgNovelRatingKeyword.layoutParams as ConstraintLayout.LayoutParams

            when (ivNovelRatingKeywordToggle.isSelected) {
                true ->
                    layoutParams.matchConstraintMaxHeight =
                        ConstraintLayout.LayoutParams.WRAP_CONTENT

                false -> layoutParams.matchConstraintMaxHeight = 78.toIntScaledByDp()
            }
            wcgNovelRatingKeyword.layoutParams = layoutParams
        }
    }

    fun setWebsosoChip(category: DetailExploreKeywordModel.CategoryModel) {
        binding.apply {
            tvRatingKeyword.text = category.categoryName
            ivNovelRatingKeyword.load(category.categoryImage)
            setupWebsosoChips(category)
        }
    }

    private fun ItemNovelRatingKeywordBinding.setupWebsosoChips(category: DetailExploreKeywordModel.CategoryModel) {
        wcgNovelRatingKeyword.removeAllViews()
        category.keywords.forEach { keyword ->
            WebsosoChip(binding.root.context).apply {
                setWebsosoChipText(keyword.keywordName)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(40f)
                setOnWebsosoChipClick { onKeywordClick(keyword, this.isSelected) }
                isSelected = keyword.isSelected
            }.also { websosoChip -> wcgNovelRatingKeyword.addChip(websosoChip) }
        }
        _isChipSetting = true
    }

    fun updateChipState(keywords: List<DetailExploreKeywordModel.CategoryModel.KeywordModel>) {
        val keywordSelectionMap =
            keywords.associateBy({ it.keywordName }, { it.isSelected })

        for (i in 0 until binding.wcgNovelRatingKeyword.childCount) {
            val chip = binding.wcgNovelRatingKeyword.getChildAt(i) as? WebsosoChip
            if (chip != null) {
                chip.isSelected = keywordSelectionMap[chip.text] ?: false
            }
        }
    }
}