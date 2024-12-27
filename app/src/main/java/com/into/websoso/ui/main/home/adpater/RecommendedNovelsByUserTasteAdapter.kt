package com.into.websoso.ui.main.home.adpater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity

class RecommendedNovelsByUserTasteAdapter(
    private val onRecommendedNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<RecommendedNovelByUserTasteEntity, RecommendedNovelsByUserTasteViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecommendedNovelsByUserTasteViewHolder {
        return RecommendedNovelsByUserTasteViewHolder.of(parent, onRecommendedNovelClick)
    }

    override fun onBindViewHolder(holder: RecommendedNovelsByUserTasteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil =
            object : DiffUtil.ItemCallback<RecommendedNovelByUserTasteEntity>() {

                override fun areItemsTheSame(
                    oldItem: RecommendedNovelByUserTasteEntity,
                    newItem: RecommendedNovelByUserTasteEntity,
                ): Boolean {
                    return oldItem.novelId == newItem.novelId
                }

                override fun areContentsTheSame(
                    oldItem: RecommendedNovelByUserTasteEntity,
                    newItem: RecommendedNovelByUserTasteEntity,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}