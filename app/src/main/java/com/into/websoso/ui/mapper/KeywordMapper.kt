package com.into.websoso.ui.mapper

import com.into.websoso.core.common.ui.model.CategoriesModel
import com.into.websoso.data.model.CategoriesEntity

fun CategoriesEntity.CategoryEntity.toUi(): CategoriesModel.CategoryModel =
    CategoriesModel.CategoryModel(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toUi() },
    )

fun CategoriesEntity.CategoryEntity.KeywordEntity.toUi(): CategoriesModel.CategoryModel.KeywordModel =
    CategoriesModel.CategoryModel.KeywordModel(
        keywordId = keywordId,
        keywordName = keywordName,
        isSelected = false,
    )

fun CategoriesModel.CategoryModel.KeywordModel.toData(): CategoriesEntity.CategoryEntity.KeywordEntity =
    CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
