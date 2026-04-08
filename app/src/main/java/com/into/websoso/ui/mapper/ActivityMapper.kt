package com.into.websoso.ui.mapper

import com.into.websoso.data.model.UserFeedsEntity.UserFeedEntity
import com.into.websoso.ui.main.myPage.model.ActivitiesModel.ActivityModel

fun UserFeedEntity.toUi(): ActivityModel =
    ActivityModel(
        feedId = feedId,
        isSpoiler = isSpoiler,
        feedContent = feedContent,
        createdDate = createdDate,
        isModified = isModified,
        isLiked = isLiked,
        isPublic = isPublic,
        likeCount = likeCount,
        commentCount = commentCount,
        novelId = novelId,
        title = title,
        novelRatingCount = novelRatingCount,
        novelRating = novelRating,
    )

fun List<UserFeedEntity>.toUi(): List<ActivityModel> = map { it.toUi() }
