package com.into.websoso.ui.main.home.adpater

import android.util.Patterns
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.resource.R.drawable.img_loading_thumbnail
import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.databinding.ItemPopularFeedBinding
import com.into.websoso.databinding.ItemPopularFeedSlotBinding
import com.into.websoso.ui.feedDetail.model.Genre as FeedDetailGenre

class PopularFeedsViewHolder(
    private val binding: ItemPopularFeedBinding,
    private val onFeedClick: (feedId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private val slots by lazy {
        listOf(
            binding.itemPopularFeesSlot1,
            binding.itemPopularFeesSlot2,
        )
    }

    fun bind(feedItems: List<PopularFeedEntity>) {
        binding.viewPopularFeedDivider1.visibility =
            if (feedItems.size > 1) View.VISIBLE else View.GONE

        slots.forEachIndexed { index, slotBinding ->
            val feed = feedItems.getOrNull(index)
            if (feed == null) {
                slotBinding.root.visibility = View.INVISIBLE
                slotBinding.root.setOnClickListener(null)
                return@forEachIndexed
            }

            slotBinding.root.visibility = View.VISIBLE
            slotBinding.bind(feed)
        }
    }

    private fun ItemPopularFeedSlotBinding.bind(feed: PopularFeedEntity) {
        tvPopularFeedTitle.text = feed.novelTitle.ellipsizeByLength()
        tvPopularFeedContent.text = if (feed.isSpoiler) "" else feed.feesContent
        tvPopularFeedContent.visibility = if (feed.isSpoiler) View.GONE else View.VISIBLE
        tvPopularFeedContentSpoiler.visibility = if (feed.isSpoiler) View.VISIBLE else View.GONE
        ivPopularFeedThumbnail.load(feed.novelImage.toImageUrl()) {
            crossfade(true)
            transformations(RoundedCornersTransformation(8f.toFloatPxFromDp()))
            error(img_loading_thumbnail)
        }
        val isGenreVisible = feed.novelGenre.isNotBlank()
        ivPopularFeedGenre.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
        if (isGenreVisible) {
            ivPopularFeedGenre.setImageResource(FeedDetailGenre.from(feed.novelGenre).drawableRes)
        }
        root.setOnClickListener { onFeedClick(feed.feedId) }
    }

    private fun String.toImageUrl(): String =
        when {
            isBlank() -> ""
            Patterns.WEB_URL.matcher(this).matches() -> this
            else -> itemView.getS3ImageUrl(this)
        }

    private fun String.ellipsizeByLength(): String {
        var titleLength = 0
        val title = StringBuilder()

        for (char in this) {
            if (!char.isWhitespace()) {
                if (titleLength == MAX_TITLE_LENGTH) return title.toString().trimEnd() + "…"
                titleLength++
            }
            title.append(char)
        }
        return this
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 16
    }
}
