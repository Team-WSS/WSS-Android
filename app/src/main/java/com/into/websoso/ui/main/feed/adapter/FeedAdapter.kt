package com.into.websoso.ui.main.feed.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.ui.main.feed.FeedItemClickListener
import com.into.websoso.ui.main.feed.adapter.FeedType.Feed
import com.into.websoso.ui.main.feed.adapter.FeedType.ItemType
import com.into.websoso.ui.main.feed.adapter.FeedType.ItemType.FEED
import com.into.websoso.ui.main.feed.adapter.FeedType.ItemType.LOADING
import com.into.websoso.ui.main.feed.adapter.FeedType.ItemType.NO_MORE
import com.into.websoso.ui.main.feed.adapter.FeedType.Loading
import com.into.websoso.ui.main.feed.adapter.FeedType.NoMore

class FeedAdapter(
    private val feedItemClickListener: FeedItemClickListener,
) : ListAdapter<FeedType, RecyclerView.ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Feed -> FEED.ordinal
        is Loading -> LOADING.ordinal
        is NoMore -> NO_MORE.ordinal
    }

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is Feed -> (getItem(position) as Feed).feed.id
        else -> super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (ItemType.valueOf(viewType)) {
            FEED -> FeedViewHolder.of(parent, feedItemClickListener)
            LOADING -> FeedLoadingViewHolder.from(parent)
            NO_MORE -> FeedNoMoreViewHolder.from(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedViewHolder -> holder.bind((getItem(position) as Feed).feed)
            else -> return
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<FeedType>() {
            override fun areItemsTheSame(oldItem: FeedType, newItem: FeedType): Boolean =
                when ((oldItem is Feed) and (newItem is Feed)) {
                    true -> (oldItem as Feed).feed.id == (newItem as Feed).feed.id
                    false -> oldItem == newItem
                }

            override fun areContentsTheSame(oldItem: FeedType, newItem: FeedType): Boolean =
                oldItem == newItem
        }
    }
}
