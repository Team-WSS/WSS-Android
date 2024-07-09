package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.remote.response.FeedResponseDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto

fun FeedsResponseDto.toData(): FeedsEntity = FeedsEntity(
    category = category,
    isLoadable = isLoadable,
    feeds = feedsResponseDto.map { it.toData() },
)

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
    isSpoiler = isSpoiler,
    isMyFeed = isMyFeed,
    novel = FeedEntity.NovelEntity(
        id = novelId,
        title = title,
        rating = novelRating,
        ratingCount = novelRatingCount,
    ),
)
