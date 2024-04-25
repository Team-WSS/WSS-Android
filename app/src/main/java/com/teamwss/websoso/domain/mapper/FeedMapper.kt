package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.domain.model.Feeds

object FeedMapper {

    fun FeedsEntity.toDomain(): Feeds = Feeds(
        category = category,
        feeds = feeds.map { it.toDomain() },
    )

    fun FeedEntity.toDomain(): Feed = Feed(
        user = Feed.User(
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
        novel = Feed.Novel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount
        )
    )
}