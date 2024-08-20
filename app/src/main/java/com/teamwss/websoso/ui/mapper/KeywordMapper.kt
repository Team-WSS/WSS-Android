package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.CategoriesEntity
import com.teamwss.websoso.common.ui.model.CategoriesModel

fun CategoriesEntity.CategoryEntity.toUi(): CategoriesModel.CategoryModel {
    return CategoriesModel.CategoryModel(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toUi() },
    )
}

fun CategoriesEntity.CategoryEntity.KeywordEntity.toUi(): CategoriesModel.CategoryModel.KeywordModel {
    return CategoriesModel.CategoryModel.KeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
        isSelected = false,
    )
}

fun CategoriesModel.CategoryModel.KeywordModel.toData(): CategoriesEntity.CategoryEntity.KeywordEntity {
    return CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
