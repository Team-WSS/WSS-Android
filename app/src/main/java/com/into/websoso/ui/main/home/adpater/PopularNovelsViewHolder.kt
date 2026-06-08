package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.into.websoso.databinding.ItemPopularNovelBinding

class PopularNovelsViewHolder(
    private val binding: ItemPopularNovelBinding,
    onPopularNovelClick: (novelId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.onClick = onPopularNovelClick
    }

    fun bind(popularNovel: PopularNovelEntity) {
        val avatarImage =
            if (popularNovel.hasUserFeed) itemView.getS3ImageUrl(popularNovel.avatarImage.orEmpty()) else ""
        val feedDescription = when {
            popularNovel.hasUserFeed -> popularNovel.feedContent.orEmpty()
            popularNovel.novelDescription.isNotBlank() -> popularNovel.novelDescription
            else -> popularNovel.feedContent.orEmpty()
        }
        with(binding) {
            ivPopularNovelAvatar.visibility =
                if (popularNovel.hasUserFeed) View.VISIBLE else View.INVISIBLE
            tvPopularNovelInShortTitle.visibility =
                if (popularNovel.hasUserFeed) View.VISIBLE else View.INVISIBLE
            ivPopularNovelAvatarNull.visibility =
                if (popularNovel.hasUserFeed) View.GONE else View.VISIBLE
            tvPopularNovelInShortTitleNull.visibility =
                if (popularNovel.hasUserFeed) View.GONE else View.VISIBLE
        }
        val updatedPopularNovel = popularNovel.copy(
            avatarImage = avatarImage,
        )
        binding.popularNovel = updatedPopularNovel
        binding.executePendingBindings()
        binding.tvPopularNovelFeedDescription.text = feedDescription
    }

    companion object {
        fun of(
            parent: ViewGroup,
            onPopularNovelClick: (novelId: Long) -> Unit,
        ): PopularNovelsViewHolder {
            val binding = ItemPopularNovelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return PopularNovelsViewHolder(binding, onPopularNovelClick)
        }
    }
}
