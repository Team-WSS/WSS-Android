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
            // TODO: 이미지 존재 여부에 따른 가시성 설정
            val imageUrls = List<String>(5) {
                "https://upload.wikimedia.org/wikipedia/ko/9/9e/%EB%A0%88%EB%94%94_%ED%94%8C%EB%A0%88%EC%9D%B4%EC%96%B4_%EC%9B%90_%EC%98%81%ED%99%94.jpg"
            }
            AdaptationFeedImageContainer(imageUrls) { index ->
                feedDetailClickListener.onFeedImageClick(imageUrls, index)
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
