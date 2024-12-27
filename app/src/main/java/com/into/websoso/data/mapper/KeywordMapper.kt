package com.into.websoso.data.mapper

import com.into.websoso.data.model.CategoriesEntity
import com.into.websoso.data.remote.response.KeywordsResponseDto

fun KeywordsResponseDto.toData(): CategoriesEntity {
    return CategoriesEntity(
        categories = categories.map { it.toData() },
    )
}

fun KeywordsResponseDto.CategoryResponseDto.toData(): CategoriesEntity.CategoryEntity {
    return CategoriesEntity.CategoryEntity(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toData() },
    )
}

fun KeywordsResponseDto.CategoryResponseDto.KeywordResponseDto.toData(): CategoriesEntity.CategoryEntity.KeywordEntity {
    return CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
}
