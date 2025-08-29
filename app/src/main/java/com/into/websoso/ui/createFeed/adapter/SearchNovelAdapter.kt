package com.into.websoso.ui.createFeed.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.ItemType
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.ItemType.LOADING
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.ItemType.NOVELS
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.Loading
import com.into.websoso.ui.createFeed.adapter.SearchNovelItemType.Novels

class SearchNovelAdapter(
    private val onNovelClick: (novelId: Long) -> (Unit),
) : ListAdapter<SearchNovelItemType, RecyclerView.ViewHolder>(diffCallBack) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Novels -> NOVELS.ordinal
            is Loading -> LOADING.ordinal
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (ItemType.valueOf(viewType)) {
            NOVELS -> SearchedNovelViewHolder.of(parent, onNovelClick)
            LOADING -> SearchNovelLoadingViewHolder.from(parent)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is SearchedNovelViewHolder -> holder.bind((getItem(position) as Novels).novel)
            is SearchNovelLoadingViewHolder -> return
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<SearchNovelItemType>() {
            override fun areItemsTheSame(
                oldItem: SearchNovelItemType,
                newItem: SearchNovelItemType,
            ): Boolean =
                when {
                    oldItem is Novels && newItem is Novels -> oldItem.novel.id == newItem.novel.id
                    else -> false
                }

            override fun areContentsTheSame(
                oldItem: SearchNovelItemType,
                newItem: SearchNovelItemType,
            ): Boolean = oldItem == newItem
        }
    }
}
