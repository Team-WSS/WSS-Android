package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.KeywordsEntity
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel

fun KeywordsEntity.CategoryEntity.toUi(): DetailExploreKeywordModel.CategoryModel {
    return DetailExploreKeywordModel.CategoryModel(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toUi() },
    )
}

fun KeywordsEntity.CategoryEntity.KeywordEntity.toUi(): DetailExploreKeywordModel.CategoryModel.KeywordModel {
    return DetailExploreKeywordModel.CategoryModel.KeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
        isSelected = false,
    )
}
