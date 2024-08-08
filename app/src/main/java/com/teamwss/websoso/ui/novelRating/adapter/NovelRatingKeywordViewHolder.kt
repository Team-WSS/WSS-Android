package com.teamwss.websoso.ui.novelRating.adapter

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.common.model.KeywordsModel

class NovelRatingKeywordViewHolder(
    private val binding: ItemNovelRatingKeywordBinding,
    private val onKeywordClick: (
        keyword: KeywordsModel.CategoryModel.KeywordModel,
        isClicked: Boolean,
    ) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: KeywordsModel.CategoryModel) {
        binding.apply {
            tvRatingKeyword.text = category.categoryName
            categoryImageUrl = category.categoryImage
            setupWebsosoChips(category)
            setupExpandToggleBtn()
        }
    }

    private fun ItemNovelRatingKeywordBinding.setupWebsosoChips(category: KeywordsModel.CategoryModel) {
        wcgNovelRatingKeyword.removeAllViews()
        category.keywords.forEach { keyword ->
            WebsosoChip(binding.root.context)
                .apply {
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
    }

    private fun ItemNovelRatingKeywordBinding.setupExpandToggleBtn() {
        ivNovelRatingKeywordToggle.setOnClickListener {
            ivNovelRatingKeywordToggle.isSelected = !ivNovelRatingKeywordToggle.isSelected
            val layoutParams = wcgNovelRatingKeyword.layoutParams as ConstraintLayout.LayoutParams

            when (ivNovelRatingKeywordToggle.isSelected) {
                true -> layoutParams.matchConstraintMaxHeight = ConstraintLayout.LayoutParams.WRAP_CONTENT

                false -> layoutParams.matchConstraintMaxHeight = 78.toDp()
            }
            wcgNovelRatingKeyword.layoutParams = layoutParams
        }
    }

    private fun Int.toDp(): Int {
        val scale = binding.root.context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    fun updateChipSelection(keyword: KeywordsModel.CategoryModel.KeywordModel, isSelected: Boolean) {
        binding.wcgNovelRatingKeyword.forEach { view ->
            val chip = view as WebsosoChip
            if (chip.text == keyword.keywordName) {
                chip.isSelected = isSelected
            }
        }
    }
}
