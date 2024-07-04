package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordCategoryEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordEntity
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordCategoryModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel.Companion.toCharmPoint

fun NovelRatingEntity.toUi(): NovelRatingModel =
    NovelRatingModel(
        novelTitle = novelTitle,
        readStatus = readStatus,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        charmPoints = charmPoints.map { it.toCharmPoint() },
        userKeywords = userKeywords.map { it.toUi() },
    )

fun NovelRatingKeywordEntity.toUi(): NovelRatingKeywordModel =
    NovelRatingKeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
    )

fun NovelRatingKeywordCategoryEntity.toUi(): NovelRatingKeywordCategoryModel =
    NovelRatingKeywordCategoryModel(
        categoryName = categoryName,
        keywords = keywords.map { it.toUi() },
    )
