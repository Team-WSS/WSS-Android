package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordCategoryEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordEntity
import com.teamwss.websoso.data.remote.request.NovelRatingRequestDto
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordCategoriesResponseDto
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordCategoryResponseDto
import com.teamwss.websoso.data.remote.response.NovelRatingKeywordResponseDto
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
            NovelRatingKeywordEntity(
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

fun NovelRatingKeywordCategoriesResponseDto.toData(): List<NovelRatingKeywordCategoryEntity> = categories.map { it.toData() }

fun NovelRatingKeywordCategoryResponseDto.toData(): NovelRatingKeywordCategoryEntity =
    NovelRatingKeywordCategoryEntity(
        categoryName = categoryName,
        keywords = keywords.map { it.toData() },
    )

fun NovelRatingKeywordResponseDto.toData(): NovelRatingKeywordEntity =
    NovelRatingKeywordEntity(
        keywordId = keywordId,
        keywordName = keywordName,
    )
