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

        // 1. 첫번쨰 카테고리를 가져온다
        val firstCategory = feed.relevantCategories.first()
        // 2. 카테고리가 기타인지 확인한다
        val genre = if (firstCategory != "기타") {
            firstCategory
            // 3. 기타가 아니면 해당하는 장르에 맞는 색상을 반환한다.
        } else {
            // 두번째 값이 있는지를 확인
            if (feed.relevantCategories.size >= 2) {
                // 두번째 값이 있으면 해당하는 장르에 맞는 색상을 반환한다.
                feed.relevantCategories[1]
            } else {
                "기타"
            }
        }
        // 4. 기타이면 두번째 값을 가져온다
        val iconColor = Category.iconColor(genre)
        val backgroundColor = Category.backgroundColor(genre)
        binding.clFeedNovelInfo.setBackgroundTint(backgroundColor)
        binding.ivFeedNovelThumbnail.setImageTint(iconColor)
        // 6. 두번째 값이 없으면 기타로 간주하고 아무런 색상을 반환하지 않는다.
        // 7. 두번째 값도 기타이면 마찬가지로 아무런 색상을 반환하지 않는다.

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
