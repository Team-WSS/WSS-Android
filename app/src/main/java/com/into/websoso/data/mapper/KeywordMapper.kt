package com.into.websoso.data.mapper

import com.into.websoso.data.model.CategoriesEntity
import com.into.websoso.data.remote.response.KeywordsResponseDto
import com.into.websoso.data.remote.response.PopularKeywordsResponseDto

fun KeywordsResponseDto.toData(): CategoriesEntity =
    CategoriesEntity(
        categories = categories.map { it.toData() },
    )

fun PopularKeywordsResponseDto.toData(): List<CategoriesEntity.CategoryEntity.KeywordEntity> =
    keywords.map { it.toData() }

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

fun PopularKeywordsResponseDto.KeywordResponseDto.toData(): CategoriesEntity.CategoryEntity.KeywordEntity =
    CategoriesEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
