package com.teamwss.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordCategoryModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel

class NovelRatingKeywordAdapter(
    private val onKeywordClick: (keyword: NovelRatingKeywordModel, isClicked: Boolean) -> (Unit),
) : ListAdapter<NovelRatingKeywordCategoryModel, NovelRatingKeywordViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NovelRatingKeywordViewHolder {
        val binding = ItemNovelRatingKeywordBinding.inflate(
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
        val diffUtil = object : DiffUtil.ItemCallback<NovelRatingKeywordCategoryModel>() {
            override fun areItemsTheSame(
                oldItem: NovelRatingKeywordCategoryModel,
                newItem: NovelRatingKeywordCategoryModel,
            ): Boolean = oldItem.categoryName == newItem.categoryName

            override fun areContentsTheSame(
                oldItem: NovelRatingKeywordCategoryModel,
                newItem: NovelRatingKeywordCategoryModel,
            ): Boolean = oldItem == newItem
        }
    }
}
