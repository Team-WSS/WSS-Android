package com.into.websoso.ui.main.home.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.decode.SvgDecoder
import coil.load
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.resource.R.drawable.img_loading_thumbnail
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
            tvPopularNovelTitle.text = popularNovel.toPopularNovelTitle(tvPopularNovelTitle)
            tvPopularNovelAuthorStatus.text = popularNovel.toAuthorStatus(itemView.context)
            val genreImagePath = popularNovel.genreName.toPopularNovelGenreImagePath()
            val isGenreVisible = genreImagePath.isNotBlank()
            ivPopularNovelGenreFrame.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
            ivPopularNovelGenre.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
            if (isGenreVisible) {
                ivPopularNovelGenre.load(itemView.getS3ImageUrl(genreImagePath)) {
                    decoderFactory(SvgDecoder.Factory())
                    error(img_loading_thumbnail)
                }
            }
            popularNovel.keywords.bindPopularNovelKeywords()
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

    private fun List<String>.bindPopularNovelKeywords() {
        val popularNovelKeywords = take(POPULAR_NOVEL_KEYWORD_MAX_COUNT)
        with(binding) {
            llPopularNovelKeywords.visibility =
                if (popularNovelKeywords.isEmpty()) View.GONE else View.VISIBLE
            tvPopularNovelKeywordFirst.visibility =
                if (popularNovelKeywords.isNotEmpty()) View.VISIBLE else View.GONE
            tvPopularNovelKeywordSecond.visibility =
                if (popularNovelKeywords.size > 1) View.VISIBLE else View.GONE
            tvPopularNovelKeywordFirst.text = popularNovelKeywords.getOrNull(0).orEmpty()
            tvPopularNovelKeywordSecond.text = popularNovelKeywords.getOrNull(1).orEmpty()
        }
    }
}

private const val POPULAR_NOVEL_KEYWORD_MAX_COUNT = 2
