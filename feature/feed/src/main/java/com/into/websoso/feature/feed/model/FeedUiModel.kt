package com.into.websoso.feature.feed.model

import com.into.websoso.core.common.extensions.orDefault
import com.into.websoso.feature.feed.model.FeedUiModel.NovelUiModel
import com.into.websoso.feature.feed.model.FeedUiModel.UserUiModel
import com.into.websoso.feed.model.Feed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun Feed.toFeedUiModel(): FeedUiModel {
    val novel = if (novel.id != null) {
        NovelUiModel(
            id = novel.id.orDefault(),
            title = novel.title.orEmpty(),
            rating = novel.rating.orDefault(),
            ratingCount = novel.ratingCount.orDefault(),
            genre = NovelCategory.fromTag(genreName.orEmpty()),
            userNovelRating = userNovelRating.orDefault(),
            feedWriterNovelRating = feedWriterNovelRating.orDefault(),
        )
    } else NovelUiModel()

    return FeedUiModel(
        user = UserUiModel(
            id = user.id,
            nickname = user.nickname,
            avatarImage = user.avatarImage,
        ),
        createdDate = createdDate,
        id = id,
        content = content,
        likeCount = likeCount,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiler = isSpoiler,
        isLiked = isLiked,
        isMyFeed = isMyFeed,
        isPublic = isPublic,
        imageUrls = imageUrls.toImmutableList(),
        imageCount = imageCount,
        novel = novel,
    )
}

data class FeedUiModel(
    val user: UserUiModel = UserUiModel(),
    val createdDate: String = "",
    val id: Long = 0L,
    val content: String = "",
    val relevantCategories: ImmutableList<String> = persistentListOf(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isModified: Boolean = false,
    val isSpoiler: Boolean = false,
    val isLiked: Boolean = false,
    val isMyFeed: Boolean = false,
    val isPublic: Boolean = false,
    val imageUrls: ImmutableList<String> = persistentListOf(),
    val imageCount: Int = 0,
    val novel: NovelUiModel = NovelUiModel(),
) {
    val isVisible: Boolean get() = !isSpoiler && imageUrls.isNotEmpty()

    data class UserUiModel(
        val id: Long = 0L,
        val nickname: String = "",
        val avatarImage: String = "",
    )

    data class NovelUiModel(
        val id: Long = 0L,
        val title: String = "",
        val rating: Float = 0f,
        val ratingCount: Int = 0,
        val userNovelRating: Float = 0f,
        val feedWriterNovelRating: Float = 0f,
        val genre: NovelCategory = NovelCategory.LIGHT_NOVEL,
    )
}
