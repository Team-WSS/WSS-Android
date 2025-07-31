package com.into.websoso.feature.library.mapper

import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoints.Companion.toAttractivePoints
import com.into.websoso.domain.library.model.NovelRating
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.model.NovelUiModel
import com.into.websoso.feature.library.model.ReadStatusUiModel

internal fun NovelEntity.toUiModel(): NovelUiModel =
    NovelUiModel(
        novelId = novelId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        novelImage = novelImage,
        readStatus = ReadStatusUiModel.from(ReadStatus.from(readStatus)),
        userNovelRating = NovelRating.from(userNovelRating).takeIf { it.rating.value > 0f },
        novelRating = NovelRating.from(novelRating),
        attractivePoints = attractivePoints.toAttractivePoints(),
        keywords = keywords,
        myFeeds = myFeeds,
        isInterest = isInterest,
    )
