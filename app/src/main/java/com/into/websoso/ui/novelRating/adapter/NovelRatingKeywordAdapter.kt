package com.into.websoso.ui.novelRating.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.databinding.ItemCommonKeywordBinding

class NovelRatingKeywordAdapter(
    private val onKeywordClick: (keyword: CategoryModel.KeywordModel, isClicked: Boolean) -> (Unit),
) : ListAdapter<CategoryModel, NovelRatingKeywordViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NovelRatingKeywordViewHolder {
        val binding = ItemCommonKeywordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return NovelRatingKeywordViewHolder(binding, onKeywordClick)
    }

    override fun onBindViewHolder(holder: NovelRatingKeywordViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            when (isChipSetting) {
                true -> updateChipState(item)
                false -> initKeywordView(item)
            }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<CategoryModel>() {

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
                return oldItem == newItem
            }
        }
    }
}
