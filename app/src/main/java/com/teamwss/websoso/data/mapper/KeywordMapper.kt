package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.KeywordsEntity
import com.teamwss.websoso.data.remote.response.KeywordResponseDto

fun KeywordResponseDto.toData(): KeywordsEntity {
    return KeywordsEntity(
        categories = categories.map { it.toData() },
    )
}

fun KeywordResponseDto.CategoryResponseDto.toData(): KeywordsEntity.CategoryEntity {
    return KeywordsEntity.CategoryEntity(
        categoryName = categoryName,
        categoryImage = categoryImage,
        keywords = keywords.map { it.toData() },
    )
}

fun KeywordResponseDto.CategoryResponseDto.KeywordResponseDto.toData(): KeywordsEntity.CategoryEntity.KeywordEntity {
    return KeywordsEntity.CategoryEntity.KeywordEntity(
        keywordId = keywordId.toInt(),
        keywordName = keywordName,
    )
}
