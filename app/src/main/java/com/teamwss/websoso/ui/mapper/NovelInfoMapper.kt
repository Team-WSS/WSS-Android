package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel

fun NovelDetailResponseDto.toUi(): NovelRatingModel {
    return NovelRatingModel(
        userNovelId = userNovelId,
        novelTitle = novelTitle,
        userNovelRating = userNovelRating,
        readStatus = readStatus,
        startDate = startDate,
        endDate = endDate,
    )
}
