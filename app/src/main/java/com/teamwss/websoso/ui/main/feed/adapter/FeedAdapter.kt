package com.teamwss.websoso.ui.main.feed.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.main.feed.FeedItemClickListener
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.Feed
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.ItemType.FEED
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.ItemType.LOADING
import com.teamwss.websoso.ui.main.feed.adapter.FeedType.Loading

class FeedAdapter(
    private val feedItemClickListener: FeedItemClickListener,
) : ListAdapter<FeedType, RecyclerView.ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Feed -> FEED.ordinal
        is Loading -> LOADING.ordinal
    }

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is Feed -> (getItem(position) as Feed).feed.id
        is Loading -> super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (FeedType.ItemType.valueOf(viewType)) {
            FEED -> FeedViewHolder.of(parent, feedItemClickListener)
            LOADING -> FeedLoadingViewHolder.from(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedViewHolder -> holder.bind((getItem(position) as Feed).feed)
            is FeedLoadingViewHolder -> return
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
