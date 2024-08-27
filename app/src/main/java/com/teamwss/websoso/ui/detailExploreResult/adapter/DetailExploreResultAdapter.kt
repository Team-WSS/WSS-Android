package com.teamwss.websoso.ui.detailExploreResult.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Header
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.HEADER
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.ItemType.RESULT
import com.teamwss.websoso.ui.detailExploreResult.adapter.DetailExploreResultItemType.Result

class DetailExploreResultAdapter(
    private val onNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<DetailExploreResultItemType, RecyclerView.ViewHolder>(diffCallBack) {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            HEADER_POSITION -> HEADER.ordinal
            else -> RESULT.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.valueOf(viewType)) {
            HEADER -> DetailExploreResultHeaderViewHolder.from(parent)
            RESULT -> DetailExploreResultViewHolder.of(parent, onNovelClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailExploreResultHeaderViewHolder -> holder.bind((getItem(position) as Header).novelCount)
            is DetailExploreResultViewHolder -> holder.bind((getItem(position) as Result).novel)
        }
    }

    companion object {
        private const val HEADER_POSITION = 0

        private val diffCallBack = object : DiffUtil.ItemCallback<DetailExploreResultItemType>() {

            override fun areItemsTheSame(
                oldItem: DetailExploreResultItemType,
                newItem: DetailExploreResultItemType,
            ): Boolean {
                return when {
                    oldItem is Result && newItem is Result -> oldItem.novel.id == newItem.novel.id
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
