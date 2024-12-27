package com.into.websoso.ui.detailExploreResult.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.HEADER
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.LOADING
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.NOVELS
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Loading
import com.into.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Novels

class DetailExploreResultAdapter(
    private val onNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<DetailExploreResultItemType, RecyclerView.ViewHolder>(diffCallBack) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Header -> HEADER.ordinal
            is Novels -> NOVELS.ordinal
            is Loading -> LOADING.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.valueOf(viewType)) {
            HEADER -> DetailExploreResultHeaderViewHolder.from(parent)
            NOVELS -> DetailExploreResultViewHolder.of(parent, onNovelClick)
            LOADING -> DetailExploreLoadingViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailExploreResultHeaderViewHolder -> holder.bind((getItem(position) as Header).novelCount)
            is DetailExploreResultViewHolder -> holder.bind((getItem(position) as Novels).novel)
            is DetailExploreLoadingViewHolder -> return
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<DetailExploreResultItemType>() {

            override fun areItemsTheSame(
                oldItem: DetailExploreResultItemType,
                newItem: DetailExploreResultItemType,
            ): Boolean {
                return when {
                    oldItem is Novels && newItem is Novels -> oldItem.novel.id == newItem.novel.id
                    oldItem is Header && newItem is Header -> oldItem.novelCount == newItem.novelCount
                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: DetailExploreResultItemType,
                newItem: DetailExploreResultItemType,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
