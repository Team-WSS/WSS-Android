package com.into.websoso.data.library.model

import com.into.websoso.core.database.entity.InDatabaseNovelEntity

data class UserNovelsEntity(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovels: List<NovelEntity>,
)

data class NovelEntity(
    val userNovelId: Long,
    val novelId: Long,
    val title: String,
    val novelImage: String,
    val novelRating: Float,
    val readStatus: String,
    val isInterest: Boolean,
    val userNovelRating: Float,
    val attractivePoints: List<String>,
    val startDate: String,
    val endDate: String,
    val keywords: List<String>,
    val myFeeds: List<String>,
)

fun InDatabaseNovelEntity.toData(): NovelEntity =
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
    )

internal fun NovelEntity.toDatabase(): InDatabaseNovelEntity =
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
    )
