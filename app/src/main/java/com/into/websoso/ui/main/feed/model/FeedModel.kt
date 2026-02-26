package com.into.websoso.ui.main.feed.model

import com.into.websoso.ui.novelDetail.model.Category

data class FeedModel(
    val user: UserModel,
    val createdDate: String,
    val id: Long,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val isModified: Boolean,
    val isSpoiler: Boolean,
    val isLiked: Boolean,
    val isMyFeed: Boolean,
    val isPublic: Boolean,
    val imageUrls: List<String>,
    val imageCount: Int,
    val novel: NovelModel,
    val categories: String = relevantCategories.joinToString(prefix = "", postfix = ""),
) {
    val formattedCreatedDate: String = " · $createdDate"
    val isEmptyOfRelevantCategories: Boolean = relevantCategories.isEmpty()
    val isVisible: Boolean get() = !isSpoiler && imageUrls.isNotEmpty()

    data class UserModel(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class NovelModel(
        val id: Long?,
        val title: String?,
        val rating: Float?,
        val ratingCount: Int?,
        val genre: String = "",
        val feedWriterNovelRating: Float?,
    ) {
        val isWriterRatingNoting: Boolean =
            feedWriterNovelRating == null || feedWriterNovelRating == 0f

        val isNothing: Boolean = id == null

        private val normalizedGenreName: String
            get() = genre.trim().ifEmpty { ETC }

        private val category: Category
            get() = Category.from(normalizedGenreName)

        val novelBackgroundColor: Int
            get() = category.backgroundColor

        val novelIconColor: Int
            get() = category.iconColor

        val isEtcGenre: Boolean
            get() = normalizedGenreName == ETC

        companion object {
            private const val ETC = "기타"
        }
    }
}
