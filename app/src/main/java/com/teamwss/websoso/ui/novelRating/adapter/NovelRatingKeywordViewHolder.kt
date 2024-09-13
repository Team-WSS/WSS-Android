package com.teamwss.websoso.ui.novelRating.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.ItemCommonKeywordBinding

class NovelRatingKeywordViewHolder(
    private val binding: ItemCommonKeywordBinding,
    private val onKeywordClick: (keyword: CategoryModel.KeywordModel, isClicked: Boolean) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    var isChipSetting: Boolean = false
        private set

    init {
        binding.setupExpandToggleBtn()
    }

    private fun ItemCommonKeywordBinding.setupExpandToggleBtn() {
        ivNovelRatingKeywordToggle.setOnClickListener {
            ivNovelRatingKeywordToggle.isSelected = !ivNovelRatingKeywordToggle.isSelected
            val layoutParams = wcgNovelRatingKeyword.layoutParams as ConstraintLayout.LayoutParams

            when (ivNovelRatingKeywordToggle.isSelected) {
                true -> layoutParams.matchConstraintMaxHeight =
                    ConstraintLayout.LayoutParams.WRAP_CONTENT

                false -> layoutParams.matchConstraintMaxHeight = 92.toIntPxFromDp()
            }
            wcgNovelRatingKeyword.layoutParams = layoutParams
        }
    }

    fun initKeywordView(category: CategoryModel) {
        val imageUrl: String = itemView.getS3ImageUrl(category.categoryImage)

        val updatedCategory = category.copy(
            categoryImage = imageUrl,
        )

        binding.apply {
            tvRatingKeyword.text = updatedCategory.categoryName
            categoryImageUrl = updatedCategory.categoryImage
            setupWebsosoChips(category)
        }
        isChipSetting = true
    }

    private fun ItemCommonKeywordBinding.setupWebsosoChips(category: CategoryModel) {
        wcgNovelRatingKeyword.removeAllViews()
        category.keywords.forEach { keyword ->
            WebsosoChip(binding.root.context).apply {
                setWebsosoChipText(keyword.keywordName)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                setWebsosoChipRadius(20f.toFloatPxFromDp())
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
