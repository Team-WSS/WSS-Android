package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
import com.teamwss.websoso.ui.novelRating.model.KeywordModel

object NovelRatingKeywordMapper {
    fun NovelRatingKeywordResponseDto.toUi(): KeywordModel {
        return KeywordModel(
            categories =
                categories.map { category ->
                    KeywordModel.Category(
                        categoryName = category.categoryName,
                        keywords =
                            category.keywords.map { keyword ->
                                KeywordModel.Category.Keyword(
                                    keywordId = keyword.keywordId,
                                    keywordName = keyword.keywordName,
                                )
                            },
                    )
                },
        )
    }
}
