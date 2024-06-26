package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel

fun NovelInfoResponseDto.toUi(): NovelRatingModel {
    return NovelRatingModel(
        userNovelId = userNovelId,
        novelTitle = novelTitle,
        userNovelRating = userNovelRating,
        readStatus = readStatus,
        startDate = startDate,
        endDate = endDate,
    )
}
