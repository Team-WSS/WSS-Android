package com.teamwss.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.model.KeywordModel.Category

class NovelRatingKeywordAdapter(private val onKeywordClick: (Category.Keyword, Boolean) -> (Unit)) :
    ListAdapter<Category, NovelRatingKeywordAdapter.NovelRatingKeywordViewHolder>(diffUtil) {

    class NovelRatingKeywordViewHolder(
        private val binding: ItemNovelRatingKeywordBinding,
        private val onKeywordClick: (Category.Keyword, Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.apply {
                tvRatingKeyword.text = item.categoryName
                setupWebsosoChips(item)
                setupExpandToggleBtn()
            }
        }

        private fun ItemNovelRatingKeywordBinding.setupWebsosoChips(item: Category) {
            wcgNovelRatingKeyword.removeAllViews()
            item.keywords.forEach { keyword ->
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
        }

        private fun ItemNovelRatingKeywordBinding.setupExpandToggleBtn() {
            ivNovelRatingKeywordToggle.setOnClickListener {
                ivNovelRatingKeywordToggle.isSelected = !ivNovelRatingKeywordToggle.isSelected
                val layoutParams =
                    wcgNovelRatingKeyword.layoutParams as ConstraintLayout.LayoutParams

                when (ivNovelRatingKeywordToggle.isSelected) {
                    true -> layoutParams.matchConstraintMaxHeight =
                        ConstraintLayout.LayoutParams.WRAP_CONTENT

                    false -> layoutParams.matchConstraintMaxHeight = 76.toDp()
                }
                wcgNovelRatingKeyword.layoutParams = layoutParams
            }
        }

        private fun Int.toDp(): Int {
            val scale = binding.root.context.resources.displayMetrics.density
            return (this * scale + 0.5f).toInt()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NovelRatingKeywordViewHolder {
        val binding =
            ItemNovelRatingKeywordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NovelRatingKeywordViewHolder(binding, onKeywordClick)
    }

    override fun onBindViewHolder(holder: NovelRatingKeywordViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.categoryName == newItem.categoryName
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.keywords.map { it.isSelected } == newItem.keywords.map { it.isSelected }
            }
        }
    }
}