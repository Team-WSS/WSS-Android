package com.into.websoso.core.database.datasource.library.mapper

import com.into.websoso.core.database.datasource.library.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.model.NovelEntity

internal fun NovelEntity.toNovelDatabase(index: Int): InDatabaseNovelEntity =
    InDatabaseNovelEntity(
        userNovelId = userNovelId,
        novelId = novelId,
        title = title,
        novelImage = novelImage,
        novelRating = novelRating,
        readStatus = readStatus,
        isInterest = isInterest,
        userNovelRating = userNovelRating,
        attractivePoints = attractivePoints,
        startDate = startDate,
        endDate = endDate,
        keywords = keywords,
        myFeeds = myFeeds,
        sortIndex = index,
        feeds = feeds,
    )

internal fun InDatabaseNovelEntity.toData(): NovelEntity =
    NovelEntity(
        userNovelId = userNovelId,
        novelId = novelId,
        title = title,
        novelImage = novelImage,
        novelRating = novelRating,
        readStatus = readStatus,
        isInterest = isInterest,
        userNovelRating = userNovelRating,
        attractivePoints = attractivePoints,
        startDate = startDate,
        endDate = endDate,
        keywords = keywords,
        myFeeds = myFeeds,
        feeds = feeds,
    )
