package com.teamwss.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.novelRating.model.RatingKeywordModel.CategoryModel

class NovelRatingKeywordAdapter(
    private val onKeywordClick: (keyword: CategoryModel.KeywordModel, isClicked: Boolean) -> (Unit),
) :
    ListAdapter<CategoryModel, NovelRatingKeywordViewHolder>(diffUtil) {
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
            object : DiffUtil.ItemCallback<CategoryModel>() {
                override fun areItemsTheSame(
                    oldItem: CategoryModel,
                    newItem: CategoryModel,
                ): Boolean {
                    return oldItem.categoryName == newItem.categoryName
                }

                override fun areContentsTheSame(
                    oldItem: CategoryModel,
                    newItem: CategoryModel,
                ): Boolean {
                    return oldItem.keywords.map { it.isSelected } == newItem.keywords.map { it.isSelected }
                }
            }
    }
}
