package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.domain.model.Feeds
import com.teamwss.websoso.ui.feed.model.FeedModel
import com.teamwss.websoso.ui.feed.model.FeedsModel

object FeedMapper {

    fun Feeds.toPresentation(): FeedsModel = FeedsModel(
        category = category,
        feeds = feeds.map { it.toPresentation() },
    )

    private fun Feed.toPresentation(): FeedModel = FeedModel(
        user = FeedModel.UserModel(
            id = user.id,
            nickname = user.nickname,
            profileImage = user.profileImage,
        ),
        createdDate = createdDate,
        id = id,
        content = content,
        relevantCategories = relevantCategories,
        likeCount = likeCount,
        likeUsers = likeUsers,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiled = isSpoiled,
        novel = FeedModel.NovelModel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount
        )
    )
}