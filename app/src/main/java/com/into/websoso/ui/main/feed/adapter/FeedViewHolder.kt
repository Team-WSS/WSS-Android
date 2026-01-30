package com.into.websoso.ui.main.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.databinding.ItemFeedBinding
import com.into.websoso.ui.main.feed.FeedItemClickListener
import com.into.websoso.ui.main.feed.model.FeedModel
import com.into.websoso.ui.novelDetail.model.Category
import com.into.websoso.ui.setBackgroundTint
import com.into.websoso.ui.setImageTint

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    feedItemClickListener: FeedItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = feedItemClickListener
    }

    fun bind(feed: FeedModel) {
        binding.feed = feed.copy(
            user = feed.user.copy(avatarImage = itemView.getS3ImageUrl(feed.user.avatarImage)),
        )

        val genre = feed.relevantCategories.firstOrNull { it != "기타" } ?: "기타"
        val iconColor = Category.iconColor(genre)
        val backgroundColor = Category.backgroundColor(genre)
        binding.clFeedNovelInfo.setBackgroundTint(backgroundColor)
        binding.ivFeedNovelThumbnail.setImageTint(iconColor)

        binding.clFeedLike.isSelected = feed.isLiked
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onClick: FeedItemClickListener,
        ): FeedViewHolder =
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
