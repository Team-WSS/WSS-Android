package com.into.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemFeedDetailHeaderBinding
import com.into.websoso.ui.feedDetail.FeedDetailClickListener
import com.into.websoso.ui.feedDetail.component.AdaptationFeedImageContainer
import com.into.websoso.ui.main.feed.model.FeedModel

class FeedDetailContentViewHolder(
    private val feedDetailClickListener: FeedDetailClickListener,
    private val binding: ItemFeedDetailHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = feedDetailClickListener
    }

    fun bind(feed: FeedModel) {
        binding.feed = feed.copy(
            user = feed.user.copy(avatarImage = itemView.getS3ImageUrl(feed.user.avatarImage)),
        )
        binding.clFeedLike.isSelected = feed.isLiked
        binding.cvFeedImage.setContent {
            AdaptationFeedImageContainer(feed.imageUrls) { index ->
                feedDetailClickListener.onFeedImageClick(feed.imageUrls, index)
            }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            feedDetailClickListener: FeedDetailClickListener,
        ): FeedDetailContentViewHolder =
            FeedDetailContentViewHolder(
                feedDetailClickListener,
                ItemFeedDetailHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
            )
    }
}
