package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.CategoriesEntity
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.remote.request.NovelRatingRequestDto
import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto

fun NovelRatingResponseDto.toData(): NovelRatingEntity =
    NovelRatingEntity(
        novelTitle = novelTitle,
        readStatus = status,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        charmPoints = attractivePoints,
        userKeywords =
        keywords.map { keyword ->
            CategoriesEntity.CategoryEntity.KeywordEntity(
                keywordId = keyword.keywordId,
                keywordName = keyword.keywordName,
            )
        },
    )

fun NovelRatingEntity.toData(): NovelRatingRequestDto =
    NovelRatingRequestDto(
        novelId = novelId,
        status = readStatus,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        attractivePoints = charmPoints,
        keywordIds = userKeywords.map { it.keywordId },
    )
