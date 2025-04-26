package com.into.websoso.ui.normalExplore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Header
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType.HEADER
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType.LOADING
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.ItemType.NOVELS
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Loading
import com.into.websoso.ui.normalExplore.adapter.NormalExploreItemType.Novels

class NormalExploreAdapter(
    private val novelItemClickListener: (novelId: Long) -> Unit,
    private val inquireClickListener: () -> Unit,
) : ListAdapter<NormalExploreItemType, RecyclerView.ViewHolder>(diffCallBack) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Header -> HEADER.ordinal
            is Novels -> NOVELS.ordinal
            is Loading -> LOADING.ordinal
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ItemType.valueOf(viewType)) {
            HEADER -> NormalExploreHeaderViewHolder.from(parent, inquireClickListener)
            NOVELS -> NormalExploreViewHolder.of(parent, novelItemClickListener)
            LOADING -> NormalExploreLoadingViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is NormalExploreHeaderViewHolder -> holder.bind((getItem(position) as Header).novelCount)
            is NormalExploreViewHolder -> holder.bind((getItem(position) as Novels).novel)
            is NormalExploreLoadingViewHolder -> return
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<NormalExploreItemType>() {
            override fun areItemsTheSame(
                oldItem: NormalExploreItemType,
                newItem: NormalExploreItemType,
            ): Boolean =
                when {
                    oldItem is Novels && newItem is Novels -> oldItem.novel.id == newItem.novel.id
                    oldItem is Header && newItem is Header -> oldItem.novelCount == newItem.novelCount
                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: NormalExploreItemType,
                newItem: NormalExploreItemType,
            ): Boolean = oldItem == newItem
        }
    }
}
