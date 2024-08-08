package com.teamwss.websoso.ui.normalExplore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType.HEADER
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType.RESULT
import com.teamwss.websoso.ui.normalExplore.adapter.NormalExploreItemType.Result

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit,
) : ListAdapter<NormalExploreItemType, RecyclerView.ViewHolder>(diffCallBack) {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            HEADER_POSITION -> HEADER.ordinal
            else -> RESULT.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.valueOf(viewType)) {
            HEADER -> NormalExploreHeaderViewHolder.from(parent)
            RESULT -> NormalExploreViewHolder.of(parent, novelItemClickListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NormalExploreHeaderViewHolder -> holder.bind((getItem(position) as Header).novelCount)
            is NormalExploreViewHolder -> holder.bind((getItem(position) as Result).novel)
        }
    }

    companion object {
        private const val HEADER_POSITION = 0

        private val diffCallBack = object : DiffUtil.ItemCallback<NormalExploreItemType>() {

            override fun areItemsTheSame(
                oldItem: NormalExploreItemType,
                newItem: NormalExploreItemType
            ): Boolean {
                return when {
                    oldItem is Result && newItem is Result -> oldItem.novel.id == newItem.novel.id
                    oldItem is Header && newItem is Header -> oldItem.novelCount == newItem.novelCount
                    else -> false
                }
            }

            override fun areContentsTheSame(
                oldItem: NormalExploreItemType,
                newItem: NormalExploreItemType
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}