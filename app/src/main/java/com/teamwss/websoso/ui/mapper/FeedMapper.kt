package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.FeedModel
import com.teamwss.websoso.ui.feed.model.FeedUiState.CategoryUiState

object FeedMapper {

    fun List<Category>.toPresentation(): List<CategoryUiState> {
        return map {
            CategoryUiState(
                category = it,
                isSelected = it == Category.ALL
            )
        }
    }

    fun Feed.toPresentation(): FeedModel = FeedModel(
        user = FeedModel.UserModel(
            id = user.id,
            nickname = user.nickname,
            avatarImage = user.avatarImage,
        ),
        createdDate = createdDate,
        id = id,
        content = content,
        relevantCategories = relevantCategories,
        likeCount = likeCount,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiler = isSpoiler,
        isLiked = isLiked,
        novel = FeedModel.NovelModel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount,
        ),
    )
}
