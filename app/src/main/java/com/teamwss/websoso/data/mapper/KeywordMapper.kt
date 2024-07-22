package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.KeywordsEntity
import com.teamwss.websoso.data.remote.response.KeywordsResponseDto

fun KeywordsResponseDto.toData(): KeywordsEntity {
    return KeywordsEntity(
        categories = categories.map { it.toData() },
    )
}

fun KeywordsResponseDto.CategoryResponseDto.toData(): KeywordsEntity.CategoryEntity {
    return KeywordsEntity.CategoryEntity(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toData() },
    )
}

fun KeywordsResponseDto.CategoryResponseDto.KeywordResponseDto.toData(): KeywordsEntity.CategoryEntity.KeywordEntity {
    return KeywordsEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId.toInt(),
        keywordName = keywordName,
    )
}
