package com.into.websoso.ui.main.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemFeedBinding
import com.into.websoso.ui.main.feed.FeedItemClickListener
import com.into.websoso.ui.main.feed.model.FeedModel

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    feedItemClickListener: FeedItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = feedItemClickListener
    }

    fun bind(feed: FeedModel) {
        binding.feed = feed.copy(
            user = feed.user.copy(avatarImage = itemView.getS3ImageUrl(feed.user.avatarImage))
        )
        
        binding.clFeedLike.isSelected = feed.isLiked
    }

    companion object {

        fun of(parent: ViewGroup, onClick: FeedItemClickListener): FeedViewHolder =
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
