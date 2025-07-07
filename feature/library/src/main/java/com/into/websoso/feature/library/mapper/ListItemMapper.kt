package com.into.websoso.feature.library.mapper

import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.ReadStatusUiModel

internal fun NovelEntity.toUiModel(): LibraryListItemModel =
    LibraryListItemModel(
        novelId = novelId,
        title = title,
        startDate = startDate,
        endDate = endDate,
        novelImage = novelImage,
        readStatus = ReadStatusUiModel.from(readStatus),
        userNovelRating = userNovelRating.takeIf { it > 0f },
        novelRating = novelRating,
        attractivePoints = attractivePoints.mapNotNull { key ->
            AttractivePoints.entries.find { it.key == key }?.toUiModel()
        },
        keywords = keywords,
        myFeeds = myFeeds,
        isInterest = isInterest,
    )
