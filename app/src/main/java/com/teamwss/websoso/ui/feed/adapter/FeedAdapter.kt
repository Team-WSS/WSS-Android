package com.teamwss.websoso.ui.feed.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.ui.feed.FeedItemClickListener

class FeedAdapter(
    private val feedItemClickListener: FeedItemClickListener
) : ListAdapter<FeedItemType, RecyclerView.ViewHolder>(diffCallBack) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    override fun getItemId(position: Int): Long = when (getItem(position)) {
        is FeedItemType.Feed -> (getItem(position) as FeedItemType.Feed).feed.id
        is FeedItemType.Loading -> super.getItemId(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            FeedItemType.FEED -> FeedViewHolder.of(parent, feedItemClickListener)
            FeedItemType.LOADING -> FeedLoadingViewHolder.from(parent)
            else -> throw IllegalArgumentException()
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedViewHolder -> holder.bind((getItem(position) as FeedItemType.Feed).feed)
            is FeedLoadingViewHolder -> return
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<FeedItemType>() {
            override fun areItemsTheSame(oldItem: FeedItemType, newItem: FeedItemType): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FeedItemType, newItem: FeedItemType): Boolean {
                return oldItem == newItem
            }
        }
    }
}
