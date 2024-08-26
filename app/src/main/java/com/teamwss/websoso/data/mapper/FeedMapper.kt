package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.CommentEntity
import com.teamwss.websoso.data.model.CommentsEntity
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedEntity.NovelEntity
import com.teamwss.websoso.data.model.FeedEntity.UserEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.model.PopularFeedsEntity
import com.teamwss.websoso.data.remote.response.CommentResponseDto
import com.teamwss.websoso.data.remote.response.CommentsResponseDto
import com.teamwss.websoso.data.remote.response.FeedResponseDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto
import com.teamwss.websoso.data.remote.response.PopularFeedsResponseDto

fun FeedsResponseDto.toData(): FeedsEntity = FeedsEntity(
    category = category,
    isLoadable = isLoadable,
    feeds = feedsResponseDto.map { it.toData() },
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
        id = userId.toLong(),
        nickname = nickname,
        avatarImage = avatarImage,
    ),
    commentContent = commentContent,
    commentId = commentId,
    createdDate = createdDate,
    isModified = isModified,
    isMyComment = isMyComment,
)

fun PopularFeedsResponseDto.toData(): PopularFeedsEntity {
    return PopularFeedsEntity(
        popularFeeds = popularFeeds.map { feed ->
            PopularFeedsEntity.PopularFeedEntity(
                feedId = feed.feedId,
                feesContent = feed.feedContent,
                likeCount = feed.likeCount,
                commentCount = feed.commentCount,
                isSpoiler = feed.isSpoiler
            )
        }
    )
}
