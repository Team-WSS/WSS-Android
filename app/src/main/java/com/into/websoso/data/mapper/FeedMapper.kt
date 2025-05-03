package com.into.websoso.data.mapper

import com.into.websoso.data.model.CommentEntity
import com.into.websoso.data.model.CommentsEntity
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.FeedEntity.NovelEntity
import com.into.websoso.data.model.FeedEntity.UserEntity
import com.into.websoso.data.model.FeedsEntity
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.UserInterestFeedsEntity
import com.into.websoso.data.remote.response.CommentResponseDto
import com.into.websoso.data.remote.response.CommentsResponseDto
import com.into.websoso.data.remote.response.FeedDetailResponseDto
import com.into.websoso.data.remote.response.FeedResponseDto
import com.into.websoso.data.remote.response.FeedsResponseDto
import com.into.websoso.data.remote.response.PopularFeedsResponseDto
import com.into.websoso.data.remote.response.UserInterestFeedsResponseDto

fun FeedsResponseDto.toData(): FeedsEntity = FeedsEntity(
    category = category,
    isLoadable = isLoadable,
    feeds = feeds.map { it.toData() }
)

fun FeedResponseDto.toData(): FeedEntity = FeedEntity(
    user = UserEntity(
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
    isPublic = isPublic,
    novel = NovelEntity(
        id = novelId,
        title = title,
        rating = novelRating,
        ratingCount = novelRatingCount,
    ),
)

fun CommentsResponseDto.toData(): CommentsEntity = CommentsEntity(
    comments = comments.map { it.toData() },
    commentsCount = commentsCount,
)

fun CommentResponseDto.toData(): CommentEntity = CommentEntity(
    user = UserEntity(
        id = userId,
        nickname = nickname,
        avatarImage = avatarImage,
    ),
    commentContent = commentContent,
    commentId = commentId,
    createdDate = createdDate,
    isModified = isModified,
    isMyComment = isMyComment,
    isBlocked = isBlocked,
    isHidden = isHidden,
    isSpoiler = isSpoiler,
)

fun FeedDetailResponseDto.toData(): FeedEntity = FeedEntity(
    user = UserEntity(
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
    isPublic = isPublic,
    novel = NovelEntity(
        id = novelId,
        title = title,
        rating = novelRating,
        ratingCount = novelRatingCount,
    ),
)

fun PopularFeedsResponseDto.toData(): PopularFeedsEntity {
    return PopularFeedsEntity(popularFeeds = popularFeeds.map { feed ->
        PopularFeedsEntity.PopularFeedEntity(
            feedId = feed.feedId,
            feesContent = feed.feedContent,
            likeCount = feed.likeCount,
            commentCount = feed.commentCount,
            isSpoiler = feed.isSpoiler,
        )
    })
}

fun UserInterestFeedsResponseDto.toData(): UserInterestFeedsEntity {
    return UserInterestFeedsEntity(
        userInterestFeeds = userInterestFeeds.map { feed ->
            UserInterestFeedsEntity.UserInterestFeedEntity(
                avatarImage = feed.avatarImage,
                feedContent = feed.feedContent,
                nickname = feed.nickname,
                novelId = feed.novelId,
                novelImage = feed.novelImage,
                novelRating = feed.novelRating,
                novelRatingCount = feed.novelRatingCount,
                novelTitle = feed.novelTitle,
            )
        },
        message = message,
    )
}
