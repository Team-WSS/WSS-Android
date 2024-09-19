package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.UserFeedsEntity.UserFeedEntity
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityModel

fun UserFeedEntity.toUi(): ActivityModel =
    ActivityModel(
        feedId = feedId,
        isSpoiler = isSpoiler,
        feedContent = feedContent,
        createdDate = ActivityModel.formatDate(createdDate),
        isModified = isModified,
        isLiked = isLiked,
        likeCount = likeCount,
        commentCount = commentCount,
        novelId = novelId ?: 0,
        title = title ?: "",
        novelRatingCount = novelRatingCount ?: 0,
        novelRating = novelRating ?: 0.0f,
        relevantCategories = ActivityModel.translateGenres(relevantCategories)
    )

fun List<UserFeedEntity>.toUi(): List<ActivityModel> {
    return map { it.toUi() }
}

