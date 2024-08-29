package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedDetailHeaderBinding
import com.teamwss.websoso.ui.feedDetail.FeedDetailClickListener
import com.teamwss.websoso.ui.main.feed.model.FeedModel

class FeedDetailContentViewHolder(
    feedDetailClickListener: FeedDetailClickListener,
    private val binding: ItemFeedDetailHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.onClick = feedDetailClickListener
    }

    fun bind(feed: FeedModel, commentCounts: Int) {
        binding.feed = feed
        binding.count = commentCounts
        binding.clFeedLike.isSelected = feed.isLiked
    }

    companion object {

        fun from(
            parent: ViewGroup,
            feedDetailClickListener: FeedDetailClickListener,
        ): FeedDetailContentViewHolder = FeedDetailContentViewHolder(
            feedDetailClickListener,
            ItemFeedDetailHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }
}
