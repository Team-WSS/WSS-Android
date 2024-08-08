package com.teamwss.websoso.ui.novelRating.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.common.model.KeywordsModel.CategoryModel
import com.teamwss.websoso.util.toFloatScaledByDp
import com.teamwss.websoso.util.toIntScaledByDp

class NovelRatingKeywordViewHolder(
    private val binding: ItemNovelRatingKeywordBinding,
    private val onKeywordClick: (keyword: CategoryModel.KeywordModel, isClicked: Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    var isChipSetting: Boolean = false
        private set

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

    fun initKeywordView(category: CategoryModel) {
        binding.apply {
            tvRatingKeyword.text = category.categoryName
            ivNovelRatingKeyword.load(category.categoryImage) {
                transformations(RoundedCornersTransformation(60f.toFloatScaledByDp()))
            }
            setupWebsosoChips(category)
        }
        isChipSetting = true
    }

    private fun ItemNovelRatingKeywordBinding.setupWebsosoChips(category: CategoryModel) {
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
                setOnWebsosoChipClick {
                    onKeywordClick(keyword, this.isSelected)
                }
                isSelected = keyword.isSelected
            }.also { websosoChip -> wcgNovelRatingKeyword.addChip(websosoChip) }
        }
    }

    fun updateChipState(category: CategoryModel) {
        val keywordSelectionMap =
            category.keywords.associateBy({ it.keywordName }, { it.isSelected })

        (0 until binding.wcgNovelRatingKeyword.childCount)
            .map { binding.wcgNovelRatingKeyword.getChildAt(it) }
            .filterIsInstance<WebsosoChip>()
            .forEach { chip ->
                chip.isSelected = keywordSelectionMap[chip.text] ?: false
            }
    }
}