package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.CategoriesEntity
import com.teamwss.websoso.ui.common.model.KeywordsModel

fun CategoriesEntity.CategoryEntity.toUi(): KeywordsModel.CategoryModel {
    return KeywordsModel.CategoryModel(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toUi() },
    )
}

fun CategoriesEntity.CategoryEntity.KeywordEntity.toUi(): KeywordsModel.CategoryModel.KeywordModel {
    return KeywordsModel.CategoryModel.KeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
        isSelected = false,
    )
}

fun KeywordsModel.CategoryModel.KeywordModel.toData(): CategoriesEntity.CategoryEntity.KeywordEntity {
    return CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
