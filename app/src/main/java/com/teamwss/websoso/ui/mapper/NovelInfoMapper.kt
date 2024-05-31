package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel

object NovelInfoMapper {
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