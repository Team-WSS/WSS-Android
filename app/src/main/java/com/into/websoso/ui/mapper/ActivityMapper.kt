package com.into.websoso.ui.mapper

import com.into.websoso.data.model.UserFeedsEntity.UserFeedEntity
import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel

fun UserFeedEntity.toUi(): ActivityModel =
    ActivityModel(
        feedId = feedId,
        isSpoiler = isSpoiler,
        feedContent = feedContent,
        createdDate = ActivityModel.formatDate(createdDate),
        isModified = isModified,
        isLiked = isLiked,
        isPublic = isPublic,
        likeCount = likeCount,
        commentCount = commentCount,
        novelId = novelId,
        title = title,
        novelRatingCount = novelRatingCount,
        novelRating = novelRating,
        relevantCategories = ActivityModel.translateGenres(relevantCategories ?: emptyList()),
    )

fun List<UserFeedEntity>.toUi(): List<ActivityModel> {
    return map { it.toUi() }
}

