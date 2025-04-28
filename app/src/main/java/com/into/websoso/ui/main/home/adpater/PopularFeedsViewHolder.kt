package com.into.websoso.ui.main.home.adpater

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.data.model.PopularFeedsEntity.PopularFeedEntity
import com.into.websoso.databinding.ItemPopularFeedBinding

class PopularFeedsViewHolder(
    private val binding: ItemPopularFeedBinding,
    private val onFeedClick: (feedId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val slots by lazy {
        listOf(
            binding.itemPopularFeesSlot1,
            binding.itemPopularFeesSlot2,
            binding.itemPopularFeesSlot3,
        )
    }

    fun bind(feedItems: List<PopularFeedEntity>) {
        slots.forEachIndexed { index, slotBinding ->
            slotBinding.apply {
                feedItems.getOrNull(index)?.let { feed ->
                    tvPopularFeedContent.text = feed.feesContent
                    tvPopularFeedLikeCount.text = feed.likeCount.toString()
                    tvPopularFeedCommentCount.text = feed.commentCount.toString()
                    tvPopularFeedContent.visibility =
                        if (feed.isSpoiler) View.INVISIBLE else View.VISIBLE
                    tvPopularFeedContentSpoiler.visibility =
                        if (feed.isSpoiler) View.VISIBLE else View.GONE
                    root.setOnClickListener { onFeedClick(feed.feedId) }
                }
            }
        }
    }
}
