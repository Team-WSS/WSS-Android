package com.into.websoso.domain.mapper

import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.FeedsEntity
import com.into.websoso.domain.model.Feed
import com.into.websoso.domain.model.Feeds

fun FeedsEntity.toDomain(): Feeds =
    Feeds(
        category = category,
        isLoadable = isLoadable,
        feeds = feeds.map { it.toDomain() },
    )

fun FeedEntity.toDomain(): Feed =
    Feed(
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
        isPublic = isPublic,
        novel = Feed.Novel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount,
        ),
    )
