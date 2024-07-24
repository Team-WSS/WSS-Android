package com.teamwss.websoso.data.model

data class NovelDetailEntity(
    val userNovelId: Long?,
    val novelTitle: String,
    val novelImage: String,
    val novelGenres: String,
    val novelGenreImage: String,
    val isNovelCompleted: Boolean,
    val author: String,
    val interestCount: Int,
    val novelRating: Float,
    val novelRatingCount: Int,
    val feedCount: Int,
    val userNovelRating: Float,
    val readStatus: String?,
    val startDate: String?,
    val endDate: String?,
    val isUserNovelInterest: Boolean,
)
