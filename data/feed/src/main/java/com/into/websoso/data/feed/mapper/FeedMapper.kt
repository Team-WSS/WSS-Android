package com.into.websoso.data.feed.mapper

import com.into.websoso.core.network.datasource.feed.model.response.CommentResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.CommentsResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedDetailResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.PopularFeedsResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.UserInterestFeedsResponseDto
import com.into.websoso.data.feed.model.CommentEntity
import com.into.websoso.data.feed.model.CommentsEntity
import com.into.websoso.data.feed.model.FeedDetailEntity
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import com.into.websoso.data.feed.model.PopularFeedsEntity
import com.into.websoso.data.feed.model.UserInterestFeedsEntity

fun FeedsResponseDto.toData(): FeedsEntity =
    FeedsEntity(
        category = category,
        isLoadable = isLoadable,
        feeds = feeds.map { it.toData() },
    )

fun FeedDetailResponseDto.toData(): FeedDetailEntity {
    return FeedDetailEntity(
        id = feedId,
        content = feedContent,
        createdDate = createdDate,
        isModified = isModified,
        isSpoiler = isSpoiler,
        isPublic = isPublic,
        isLiked = isLiked,
        likeCount = likeCount,
        commentCount = commentCount,
        isMyFeed = isMyFeed,
        relevantCategories = relevantCategories,
        images = images,
        imageCount = images.size,
        user = FeedDetailEntity.UserEntity(
            id = userId,
            nickname = nickname,
            avatarImage = avatarImage,
        ),

        novel = novelId?.let { id ->
            FeedDetailEntity.NovelEntity(
                id = id,
                title = title.orEmpty(),
                rating = novelRating,
                ratingCount = novelRatingCount ?: 0,
                genre = novelGenre.orEmpty(),
                feedWriterNovelRating = feedWriterNovelRating,
                thumbnail = this.images.firstOrNull().orEmpty(),
                author = novelAuthor.orEmpty(),
                description = novelDescription.orEmpty(),
            )
        },
    )
}

fun FeedResponseDto.toData(): FeedEntity =
    FeedEntity(
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
        isPublic = isPublic,
        images = thumbnailUrl?.let { listOf(it) } ?: emptyList(),
        imageCount = imageCount,
        novel = FeedEntity.NovelEntity(
            id = novelId,
            title = title,
            rating = novelRating,
            ratingCount = novelRatingCount,
        ),
        genreName = genreName,
        userNovelRating = userNovelRating,
        feedWriterNovelRating = feedWriterNovelRating,
    )

fun CommentsResponseDto.toData(): CommentsEntity =
    CommentsEntity(
        comments = comments.map { it.toData() },
        commentsCount = commentsCount,
    )

fun CommentResponseDto.toData(): CommentEntity =
    CommentEntity(
        user = FeedEntity.UserEntity(
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

fun PopularFeedsResponseDto.toData(): PopularFeedsEntity =
    PopularFeedsEntity(
        popularFeeds = popularFeeds.map { feed ->
            PopularFeedsEntity.PopularFeedEntity(
                feedId = feed.feedId,
                feesContent = feed.feedContent,
                likeCount = feed.likeCount,
                commentCount = feed.commentCount,
                isSpoiler = feed.isSpoiler,
            )
        },
    )

fun UserInterestFeedsResponseDto.toData(): UserInterestFeedsEntity =
    UserInterestFeedsEntity(
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
