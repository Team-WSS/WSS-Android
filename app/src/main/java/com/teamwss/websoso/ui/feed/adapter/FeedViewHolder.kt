package com.teamwss.websoso.ui.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedBinding
import com.teamwss.websoso.ui.feed.FeedItemClickListener
import com.teamwss.websoso.ui.feed.model.FeedModel

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    feedItemClickListener: FeedItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = feedItemClickListener
    }

    fun bind(feed: FeedModel) {
        binding.feed = feed
        binding.clFeedThumbUp.isSelected = feed.isThumbUpSelected
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
