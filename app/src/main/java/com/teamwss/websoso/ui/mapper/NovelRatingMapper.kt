package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.KeywordsEntity
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordCategoryModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel.Companion.toCharmPoint

fun NovelRatingEntity.toUi(): NovelRatingModel =
    NovelRatingModel(
        novelTitle = novelTitle ?: "",
        readStatus = readStatus,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        charmPoints = charmPoints.map { it.toCharmPoint() },
        userKeywords = userKeywords.map { it.toNovelRatingUi() },
    )

fun NovelRatingKeywordModel.toData(): KeywordsEntity.CategoryEntity.KeywordEntity =
    KeywordsEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )

fun KeywordsEntity.toNovelRatingUi(): List<NovelRatingKeywordCategoryModel> {
    return categories.map { it.toNovelRatingUi() }
}

fun KeywordsEntity.CategoryEntity.toNovelRatingUi(): NovelRatingKeywordCategoryModel {
    return NovelRatingKeywordCategoryModel(
        categoryName = categoryName,
        categoryImageUrl = categoryImage,
        keywords = keywords.map { it.toNovelRatingUi() },
    )
}

fun KeywordsEntity.CategoryEntity.KeywordEntity.toNovelRatingUi(): NovelRatingKeywordModel {
    return NovelRatingKeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
