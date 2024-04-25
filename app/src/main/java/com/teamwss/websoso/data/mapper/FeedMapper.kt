package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.remote.response.FeedResponseDto

object FeedMapper {

    fun FeedResponseDto.toData(): FeedEntity = FeedEntity(
        user = FeedEntity.UserEntity(
            id = userId,
            nickname = nickname,
            avatarImage = avatarImage,
        ),
        createdDate = createdDate,
        id = feedId,
        content = feedContent,
        relevantCategories = relevantCategories,
        likeCount = likeCount,
        isLiked = isLiked,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiled = isSpoiled,
        isMyFeed = isMyFeed,
        novel = FeedEntity.NovelEntity(
            id = novelId,
            title = title,
            rating = novelRating,
            ratingCount = novelRatingCount,
        )
    )
}
