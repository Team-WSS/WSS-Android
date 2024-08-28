package com.teamwss.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.databinding.ItemFeedDetailHeaderBinding
import com.teamwss.websoso.ui.main.feed.model.FeedModel

class FeedDetailContentViewHolder(
    private val binding: ItemFeedDetailHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feed: FeedModel, commentCounts: Int) {
        binding.feed = feed
        binding.count = commentCounts
    }

    companion object {

        fun from(parent: ViewGroup): FeedDetailContentViewHolder =
            FeedDetailContentViewHolder(
                ItemFeedDetailHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            )
    }
}

