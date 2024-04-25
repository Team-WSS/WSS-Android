package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.domain.model.Feed

object FeedMapper {

    fun FeedEntity.toDomain(): Feed = Feed(
        user = Feed.User(
            id = user.id,
            nickname = user.nickname,
            avatarImage = user.avatarImage,
        ),
        createdDate = createdDate,
        id = id,
        content = content,
        relevantCategories = relevantCategories,
        likeCount = likeCount,
        isLiked = isLiked,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiled = isSpoiled,
        isMyFeed = isMyFeed,
        novel = Feed.Novel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount,
        )
    )
}
