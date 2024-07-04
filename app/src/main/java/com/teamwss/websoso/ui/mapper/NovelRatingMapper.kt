package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordEntity
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel

fun NovelRatingEntity.toUi(): NovelRatingModel {
    return NovelRatingModel(
        novelTitle = novelTitle,
        readStatus = readStatus,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        attractivePoints = attractivePoints,
        userKeywords = userKeywords.map { it.toUi() },
    )
}

fun NovelRatingKeywordEntity.toUi(): NovelRatingKeywordModel {
    return NovelRatingKeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
