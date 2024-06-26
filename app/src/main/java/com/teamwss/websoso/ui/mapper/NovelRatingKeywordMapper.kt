package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.ui.novelRating.model.KeywordModel

fun NovelRatingKeywordResponseDto.toUi(): KeywordModel {
    return KeywordModel(
        categories = categories.map { it.toUi() },
    )
}

fun NovelRatingKeywordResponseDto.CategoryResponseDto.toUi(): KeywordModel.Category {
    return KeywordModel.Category(
        categoryName = categoryName,
        keywords = keywords.map { it.toUi() },
    )
}

fun NovelRatingKeywordResponseDto.CategoryResponseDto.KeywordResponseDto.toUi(): KeywordModel.Category.Keyword {
    return KeywordModel.Category.Keyword(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
