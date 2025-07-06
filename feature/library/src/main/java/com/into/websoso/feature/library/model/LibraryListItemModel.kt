package com.into.websoso.feature.library.model

data class LibraryListItemModel(
    val novelId: Long,
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val novelImage: String,
    val readStatus: ReadStatusUiModel?,
    val userNovelRating: Float?,
    val novelRating: Float,
    val attractivePoints: List<AttractivePointUiModel>,
    val keywords: List<String>,
    val myFeeds: List<String>,
    val isInterest: Boolean,
)
