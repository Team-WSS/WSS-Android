package com.into.websoso.feed.mapper

import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import com.into.websoso.feed.model.Feed
import com.into.websoso.feed.model.Feeds
import com.into.websoso.feed.model.UserFeeds
import com.into.websoso.user.model.MyProfileEntity
import com.into.websoso.user.model.UserFeedsEntity

fun FeedsEntity.toDomain(): Feeds =
    Feeds(
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
        likeCount = likeCount,
        isLiked = isLiked,
        commentCount = commentCount,
        isModified = isModified,
        isSpoiler = isSpoiler,
        isMyFeed = isMyFeed,
        isPublic = isPublic,
        imageUrls = images,
        imageCount = imageCount,
        novel = Feed.Novel(
            id = novel.id,
            title = novel.title,
            rating = novel.rating,
            ratingCount = novel.ratingCount,
        ),
        genreName = genreName,
        userNovelRating = userNovelRating,
        feedWriterNovelRating = feedWriterNovelRating,
    )

fun UserFeedsEntity.toDomain(): UserFeeds =
    UserFeeds(
        isLoadable = this.isLoadable,
        feeds = this.feeds.map { it.toDomain() },
    )

fun UserFeedsEntity.UserFeedEntity.toDomain(): UserFeeds.UserFeed =
    UserFeeds.UserFeed(
        feedId = this.feedId,
        isSpoiler = this.isSpoiler,
        feedContent = this.feedContent,
        createdDate = this.createdDate,
        isModified = this.isModified,
        isLiked = this.isLiked,
        isPublic = this.isPublic,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        novelId = this.novelId,
        title = this.title,
        novelRatingCount = this.novelRatingCount,
        novelRating = this.novelRating,
        genre = this.genre,
        userNovelRating = this.userNovelRating,
        thumbnailUrl = this.thumbnailUrl,
        imageCount = this.imageCount,
        feedWriterNovelRating = this.feedWriterNovelRating,
    )

fun UserFeedsEntity.UserFeedEntity.toDomain(
    id: Long,
    myProfile: MyProfileEntity,
): Feed =
    Feed(
        user = Feed.User(
            id = id,
            nickname = myProfile.nickname,
            avatarImage = myProfile.avatarImage,
        ),
        createdDate = this.createdDate,
        id = this.feedId,
        content = this.feedContent,
        likeCount = this.likeCount,
        isLiked = this.isLiked,
        commentCount = this.commentCount,
        isModified = this.isModified,
        isSpoiler = this.isSpoiler,
        isMyFeed = true,
        isPublic = this.isPublic,
        imageUrls = listOfNotNull(this.thumbnailUrl),
        imageCount = this.imageCount,
        novel = Feed.Novel(
            id = this.novelId,
            title = this.title,
            rating = this.novelRating,
            ratingCount = this.novelRatingCount,
        ),
        genreName = this.genre,
        userNovelRating = this.userNovelRating,
        feedWriterNovelRating = this.feedWriterNovelRating,
    )

fun UserFeedsEntity.UserFeedEntity.toFeedEntity(
    userProfile: MyProfileEntity,
    userId: Long,
): FeedEntity =
    FeedEntity(
        id = this.feedId,
        content = this.feedContent,
        createdDate = this.createdDate,
        isModified = this.isModified,
        isSpoiler = this.isSpoiler,
        isPublic = this.isPublic,
        isLiked = this.isLiked,
        likeCount = this.likeCount,
        commentCount = this.commentCount,
        isMyFeed = true,
        user = FeedEntity.UserEntity(
            id = userId,
            nickname = userProfile.nickname,
            avatarImage = userProfile.avatarImage,
        ),
        novel = FeedEntity.NovelEntity(
            id = this.novelId,
            title = this.title,
            rating = this.novelRating,
            ratingCount = this.novelRatingCount,
        ),
        images = if (this.thumbnailUrl != null) listOf(this.thumbnailUrl.orEmpty()) else emptyList(),
        imageCount = this.imageCount,
        genreName = this.genre,
        userNovelRating = this.userNovelRating,
        feedWriterNovelRating = this.feedWriterNovelRating,
    )
