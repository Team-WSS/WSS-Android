package com.into.websoso.user.mapper

import com.into.websoso.core.network.datasource.user.model.MyProfileResponseDto
import com.into.websoso.core.network.datasource.user.model.UserFeedsResponseDto
import com.into.websoso.core.network.datasource.user.model.UserInfoResponseDto
import com.into.websoso.user.model.MyProfileEntity
import com.into.websoso.user.model.UserFeedsEntity
import com.into.websoso.user.model.UserInfoEntity

fun UserFeedsResponseDto.toData(isDefaultFilter: Boolean): UserFeedsEntity =
    UserFeedsEntity(
        isLoadable = this.isLoadable,
        feedsCount = if (isDefaultFilter) feedsCount else this.feeds.size,
        feeds = this.feeds.map { it.toData() },
    )

fun UserFeedsResponseDto.UserFeedResponseDto.toData(): UserFeedsEntity.UserFeedEntity =
    UserFeedsEntity.UserFeedEntity(
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

fun UserInfoResponseDto.toData(): UserInfoEntity =
    UserInfoEntity(
        userId = this.userId,
        nickname = this.nickname,
        gender = this.gender,
    )

fun MyProfileResponseDto.toData(): MyProfileEntity =
    MyProfileEntity(
        nickname = this.nickname,
        intro = this.intro,
        avatarImage = this.avatarImage,
        genrePreferences = this.genrePreferences,
    )
