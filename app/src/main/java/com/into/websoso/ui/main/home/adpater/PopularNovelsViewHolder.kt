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
        bindTopInformation(popularNovel)
        bindGenreFlag(popularNovel.genreName)
        bindPopularNovelKeywords(popularNovel.keywords)
        bindFeedHeader(popularNovel.hasUserFeed)
        binding.popularNovel = popularNovel.copy(
            avatarImage = popularNovel.toAvatarImageUrl(),
        )
        binding.executePendingBindings()
        binding.tvPopularNovelFeedDescription.text = popularNovel.toFeedDescription()
    }

    private fun bindTopInformation(popularNovel: PopularNovelEntity) {
        with(binding) {
            tvPopularNovelTitle.text = popularNovel.toPopularNovelTitle(tvPopularNovelTitle)
            tvPopularNovelAuthorStatus.text = popularNovel.toAuthorStatus(itemView.context)
        }
    }

    private fun bindGenreFlag(genreName: String) {
        val genreImagePath = genreName.toPopularNovelGenreImagePath()
        val isGenreVisible = genreImagePath.isNotBlank()

        with(binding) {
            ivPopularNovelGenreFrame.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
            ivPopularNovelGenre.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
            if (isGenreVisible) {
                ivPopularNovelGenre.load(itemView.getS3ImageUrl(genreImagePath)) {
                    decoderFactory(SvgDecoder.Factory())
                    error(img_loading_thumbnail)
                }
            }
        }
    }

    private fun bindPopularNovelKeywords(keywords: List<String>) {
        val popularNovelKeywords = keywords.take(POPULAR_NOVEL_KEYWORD_MAX_COUNT)
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

    private fun bindFeedHeader(hasUserFeed: Boolean) {
        with(binding) {
            ivPopularNovelAvatar.visibility = if (hasUserFeed) View.VISIBLE else View.INVISIBLE
            tvPopularNovelInShortTitle.visibility =
                if (hasUserFeed) View.VISIBLE else View.INVISIBLE
            ivPopularNovelAvatarNull.visibility = if (hasUserFeed) View.GONE else View.VISIBLE
            tvPopularNovelInShortTitleNull.visibility = if (hasUserFeed) View.GONE else View.VISIBLE
        }
    }

    private fun PopularNovelEntity.toAvatarImageUrl(): String = if (hasUserFeed) itemView.getS3ImageUrl(avatarImage.orEmpty()) else ""

    private fun PopularNovelEntity.toFeedDescription(): String =
        when {
            hasUserFeed -> feedContent.orEmpty()
            novelDescription.isNotBlank() -> novelDescription
            else -> feedContent.orEmpty()
        }

    companion object {
        private const val POPULAR_NOVEL_KEYWORD_MAX_COUNT = 2

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
