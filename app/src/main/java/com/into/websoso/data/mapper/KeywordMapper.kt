package com.into.websoso.data.mapper

import com.into.websoso.data.model.CategoriesEntity
import com.into.websoso.data.remote.response.KeywordsResponseDto

fun KeywordsResponseDto.toData(): CategoriesEntity =
    CategoriesEntity(
        categories = categories.map { it.toData() },
    )

fun KeywordsResponseDto.CategoryResponseDto.toData(): CategoriesEntity.CategoryEntity =
    CategoriesEntity.CategoryEntity(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toData() },
    )

fun KeywordsResponseDto.CategoryResponseDto.KeywordResponseDto.toData(): CategoriesEntity.CategoryEntity.KeywordEntity =
    CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
