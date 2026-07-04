package com.into.websoso.data.filter.model

const val DEFAULT_SORT_CRITERIA = "created_desc"
const val DEFAULT_IS_INTERESTED = false
const val DEFAULT_RATING_MIN = 0.0f
const val DEFAULT_RATING_MAX = 5.0f
const val DEFAULT_IS_RATINGLESS = false

data class LibraryFilter(
    val sortCriteria: String = DEFAULT_SORT_CRITERIA,
    val isInterested: Boolean = DEFAULT_IS_INTERESTED,
    val readStatuses: List<String> = emptyList(),
    val attractivePoints: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val isComplete: Boolean? = null,
    val ratingMin: Float = DEFAULT_RATING_MIN,
    val ratingMax: Float = DEFAULT_RATING_MAX,
    val isRatingless: Boolean = DEFAULT_IS_RATINGLESS,
    val keywords: List<String> = emptyList(),
)
