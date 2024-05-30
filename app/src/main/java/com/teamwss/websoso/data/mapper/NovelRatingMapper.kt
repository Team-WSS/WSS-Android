package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel

object NovelRatingMapper {
    fun NovelInfoResponseDto.toPresentation(): NovelRatingModel {
        return NovelRatingModel(
            userNovelId = userNovelId,
            novelTitle = novelTitle,
            userNovelRating = userNovelRating,
            readStatus = readStatus,
            startDate = startDate,
            endDate = endDate
        )
    }
}