package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.ui.novelRating.model.KeywordModel

object NovelRatingKeywordMapper {
    fun NovelRatingKeywordResponseDto.toUi(): KeywordModel {
        return KeywordModel(
            categories = categories.map { it.toUi() },
        )
    }

    fun NovelRatingKeywordResponseDto.Category.toUi(): KeywordModel.Category {
        return KeywordModel.Category(
            categoryName = categoryName,
            keywords = keywords.map { it.toUi() },
        )
    }

    fun NovelRatingKeywordResponseDto.Category.Keyword.toUi(): KeywordModel.Category.Keyword {
        return KeywordModel.Category.Keyword(
            keywordId = keywordId,
            keywordName = keywordName,
        )
    }
}
