package com.teamwss.websoso.ui.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedBinding
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.ui.feed.FeedItemClickListener

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    onClick: FeedItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = onClick
    }

    fun bind(feed: Feed) {
        binding.feed = feed
    }

    companion object {
        fun from(parent: ViewGroup, onClick: FeedItemClickListener): FeedViewHolder =
            FeedViewHolder(
                ItemFeedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                onClick,
            )
    }
}
