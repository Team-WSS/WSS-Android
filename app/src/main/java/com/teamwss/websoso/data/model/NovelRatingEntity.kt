package com.teamwss.websoso.data.model

data class NovelRatingEntity(
    val novelTitle: String,
    val readStatus: String,
    val startDate: String,
    val endDate: String,
    val userNovelRating: Float,
    val attractivePoints: List<String>,
    val userKeywords: List<NovelRatingKeywordEntity>,
)
