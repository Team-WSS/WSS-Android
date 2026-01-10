package com.into.websoso.ui.feedDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemFeedDetailHeaderBinding
import com.into.websoso.ui.feedDetail.FeedDetailClickListener
import com.into.websoso.ui.feedDetail.component.AdaptationFeedImageContainer
import com.into.websoso.ui.feedDetail.model.FeedDetailModel

class FeedDetailContentViewHolder(
    private val feedDetailClickListener: FeedDetailClickListener,
    private val binding: ItemFeedDetailHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = feedDetailClickListener
    }

    fun bind(feedDetail: FeedDetailModel) {
        binding.feedDetail = feedDetail.copy(
            user = feedDetail.user?.copy(avatarImage = itemView.getS3ImageUrl(feedDetail.user.avatarImage)),
        )
        binding.clFeedLike.isSelected = feedDetail.feed?.isLiked == true
        binding.cvFeedImage.setContent {
            AdaptationFeedImageContainer(feedDetail.feed?.imageUrls ?: return@setContent) { index ->
                feedDetailClickListener.onFeedImageClick(index, feedDetail.feed.imageUrls)
            }
        }
        binding.ivFeedDetailNovelGenre.setImageResource(feedDetail.novelImage)
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
