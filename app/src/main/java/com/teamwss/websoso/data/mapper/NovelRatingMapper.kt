package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordEntity
import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto

fun NovelRatingResponseDto.toData(): NovelRatingEntity {
    return NovelRatingEntity(
        novelTitle = novelTitle,
        readStatus = status,
        startDate = startDate,
        endDate = endDate,
        userNovelRating = userNovelRating,
        attractivePoints = attractivePoints,
        userKeywords = keywords.map { keyword ->
            NovelRatingKeywordEntity(
                keywordId = keyword.keywordId,
                keywordName = keyword.keywordName,
            )
        },
    )
}