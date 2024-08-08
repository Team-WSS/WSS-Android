package com.teamwss.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.model.KeywordsModel

class NovelRatingKeywordAdapter(
    private val onKeywordClick: (keyword: KeywordsModel.CategoryModel.KeywordModel, isClicked: Boolean) -> (Unit),
) : ListAdapter<KeywordsModel.CategoryModel, NovelRatingKeywordViewHolder>(diffUtil) {
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
        val diffUtil = object : DiffUtil.ItemCallback<KeywordsModel.CategoryModel>() {
            override fun areItemsTheSame(
                oldItem: KeywordsModel.CategoryModel,
                newItem: KeywordsModel.CategoryModel,
            ): Boolean = oldItem.categoryName == newItem.categoryName

            override fun areContentsTheSame(
                oldItem: KeywordsModel.CategoryModel,
                newItem: KeywordsModel.CategoryModel,
            ): Boolean = oldItem == newItem
        }
    }
}
