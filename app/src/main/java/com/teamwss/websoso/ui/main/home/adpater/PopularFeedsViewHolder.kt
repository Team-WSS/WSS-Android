package com.teamwss.websoso.ui.main.home.adpater

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.teamwss.websoso.data.model.PopularFeedsEntity.PopularFeedEntity
import com.teamwss.websoso.databinding.ItemPopularFeedBinding

class PopularFeedsViewHolder(
    private val binding: ItemPopularFeedBinding,
    private val navigateToNovelDetail: (Long) -> Unit,
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
            slotBinding?.apply {
                feedItems.getOrNull(index)?.let { feed ->
                    tvPopularFeedContent.text = feed.feesContent
                    tvPopularFeedLikeCount.text = feed.likeCount.toString()
                    tvPopularFeedCommentCount.text = feed.commentCount.toString()
                    tvPopularFeedContentSpoiler.visibility =
                        if (feed.isSpoiler) View.VISIBLE else View.GONE
                    root.setOnClickListener { navigateToNovelDetail(feed.feedId) }
                }
            }
        }
    }
}
