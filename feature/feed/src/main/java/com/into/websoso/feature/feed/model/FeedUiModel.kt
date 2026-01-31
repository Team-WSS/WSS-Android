package com.into.websoso.feature.feed.model

import com.into.websoso.core.common.extensions.orDefault
import com.into.websoso.feature.feed.model.FeedUiModel.NovelUiModel
import com.into.websoso.feature.feed.model.FeedUiModel.UserUiModel
import com.into.websoso.feed.model.Feed
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun Feed.toFeedUiModel(): FeedUiModel {
    val novel = if (novel.id != null) {
        NovelUiModel(
            id = novel.id.orDefault(),
            title = novel.title.orEmpty(),
            rating = novel.rating.orDefault(),
            ratingCount = novel.ratingCount.orDefault(),
            genre = NovelCategory.fromTag(genreName.orEmpty()),
            userNovelRating = userNovelRating.orDefault(),
            feedWriterNovelRating = feedWriterNovelRating,
        )
    } else {
        null
    }

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
        relevantCategories = relevantCategories.toImmutableList(),
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
    val novel: NovelUiModel? = null,
) {
    val isVisible: Boolean get() = !isSpoiler && imageUrls.isNotEmpty()
    val relevantCategoriesByKr: ImmutableList<String>
        get() = relevantCategories.map { NovelCategory.fromTagToKorean(it) }.toImmutableList()
    val formattedCreatedDate: String
        get() {
            if (createdDate.contains("월") && createdDate.contains("일")) {
                return createdDate
            }

            return try {
                val date = LocalDate.parse(createdDate)
                "${date.monthValue}월 ${date.dayOfMonth}일"
            } catch (e: DateTimeParseException) {
                createdDate
            }
        }

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
        val feedWriterNovelRating: Float? = null,
        val genre: NovelCategory = NovelCategory.LIGHT_NOVEL,
    )
}
