package com.teamwss.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.novelRating.model.KeywordModel.Category

class NovelRatingKeywordAdapter(
    private val onKeywordClick: (keyword: Category.Keyword, isClicked: Boolean) -> (Unit),
) :
    ListAdapter<Category, NovelRatingKeywordViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NovelRatingKeywordViewHolder {
        val binding =
            ItemNovelRatingKeywordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return NovelRatingKeywordViewHolder(binding, onKeywordClick)
    }

    override fun onBindViewHolder(
        holder: NovelRatingKeywordViewHolder,
        position: Int,
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<Category>() {
                override fun areItemsTheSame(
                    oldItem: Category,
                    newItem: Category,
                ): Boolean {
                    return oldItem.categoryName == newItem.categoryName
                }

                override fun areContentsTheSame(
                    oldItem: Category,
                    newItem: Category,
                ): Boolean {
                    return oldItem.keywords.map { it.isSelected } == newItem.keywords.map { it.isSelected }
                }
            }
    }
}
