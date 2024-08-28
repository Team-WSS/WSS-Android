package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.MyActivitiesEntity
import com.teamwss.websoso.ui.myPage.myActivity.model.ActivityModel

fun MyActivitiesEntity.MyActivityEntity.toUi(): ActivityModel =
    ActivityModel(
        feedId = feedId,
        userId = userId,
        profileImg = profileImg,
        nickname = nickname,
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

fun List<MyActivitiesEntity.MyActivityEntity>.toUi(): List<ActivityModel> {
    return map { it.toUi() }
}

