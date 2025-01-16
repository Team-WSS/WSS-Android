package com.into.websoso.ui.detailExplore.keyword.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.databinding.ItemCommonKeywordBinding

class DetailExploreKeywordAdapter(
    private val onKeywordClick: (keywordId: Int) -> (Unit),
) : ListAdapter<CategoryModel, DetailExploreKeywordViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DetailExploreKeywordViewHolder {
        val binding = ItemCommonKeywordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return DetailExploreKeywordViewHolder(binding, onKeywordClick)
    }

    override fun onBindViewHolder(holder: DetailExploreKeywordViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            when (isChipSetting) {
                true -> updateChipState(item)
                false -> initKeywordView(item)
            }
        }
    }

    companion object {
        private val diffUtil =
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
                    return oldItem == newItem
                }
            }
    }
}
