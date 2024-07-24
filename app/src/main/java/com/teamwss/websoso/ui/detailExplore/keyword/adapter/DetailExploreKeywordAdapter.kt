package com.teamwss.websoso.ui.detailExplore.keyword.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.databinding.ItemNovelRatingKeywordBinding
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel

class DetailExploreKeywordAdapter(
    private val onKeywordClick: (keyword: DetailExploreKeywordModel.CategoryModel.KeywordModel, isClicked: Boolean) -> (Unit),
) : ListAdapter<DetailExploreKeywordModel.CategoryModel, DetailExploreKeywordViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DetailExploreKeywordViewHolder {
        val binding = ItemNovelRatingKeywordBinding.inflate(
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
                true -> updateChipState(item.keywords)
                false -> initKeywordView(item)
            }
        }
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<DetailExploreKeywordModel.CategoryModel>() {

                override fun areItemsTheSame(
                    oldItem: DetailExploreKeywordModel.CategoryModel,
                    newItem: DetailExploreKeywordModel.CategoryModel,
                ): Boolean {
                    return oldItem.categoryName == newItem.categoryName
                }

                override fun areContentsTheSame(
                    oldItem: DetailExploreKeywordModel.CategoryModel,
                    newItem: DetailExploreKeywordModel.CategoryModel,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
