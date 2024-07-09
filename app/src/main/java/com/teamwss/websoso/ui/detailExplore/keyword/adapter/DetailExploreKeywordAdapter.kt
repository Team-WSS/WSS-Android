package com.teamwss.websoso.ui.detailExplore.keyword.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.novelRating.model.RatingKeywordModel.CategoryModel

class DetailExploreKeywordAdapter(
    private val onKeywordClick: (keyword: CategoryModel.KeywordModel, isClicked: Boolean) -> (Unit),
) : ListAdapter<CategoryModel, DetailExploreKeywordViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DetailExploreKeywordViewHolder {
        val binding =
            ItemNovelRatingKeywordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return DetailExploreKeywordViewHolder(binding, onKeywordClick)
    }

    override fun onBindViewHolder(
        holder: DetailExploreKeywordViewHolder,
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
