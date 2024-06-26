package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.ui.novelRating.model.RatingKeywordModel

fun NovelRatingKeywordResponseDto.toUi(): RatingKeywordModel {
    return RatingKeywordModel(
        categories = categories.map { it.toUi() },
    )
}

fun NovelRatingKeywordResponseDto.CategoryResponseDto.toUi(): RatingKeywordModel.CategoryModel {
    return RatingKeywordModel.CategoryModel(
        categoryName = categoryName,
        keywords = keywords.map { it.toUi() },
    )
}

fun NovelRatingKeywordResponseDto.CategoryResponseDto.KeywordResponseDto.toUi(): RatingKeywordModel.CategoryModel.KeywordModel {
    return RatingKeywordModel.CategoryModel.KeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
