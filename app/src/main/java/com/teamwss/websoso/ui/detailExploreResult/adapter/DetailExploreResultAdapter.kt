package com.teamwss.websoso.ui.detailExploreResult.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity

class DetailExploreResultAdapter(
    private val onNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<NovelEntity, DetailExploreResultViewHolder>(diffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailExploreResultViewHolder {
        return DetailExploreResultViewHolder.of(parent, onNovelClick)
    }

    override fun onBindViewHolder(holder: DetailExploreResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<NovelEntity>() {

                override fun areItemsTheSame(
                    oldItem: NovelEntity,
                    newItem: NovelEntity,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: NovelEntity,
                    newItem: NovelEntity,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}