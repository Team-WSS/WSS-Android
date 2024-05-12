package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.domain.model.Feeds

object FeedMapper {

    fun FeedsEntity.toDomain(): Feeds = Feeds(
        category = category,
        isLoadable = isLoadable,
        feeds = feeds.map { it.toDomain() },
    )

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
        isSpoiler = isSpoiler,
        isMyFeed = isMyFeed,
        novel = Feed.Novel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount,
        ),
    )
}
