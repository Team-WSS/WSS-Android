package com.into.websoso.data.filter.model

const val DEFAULT_SORT_CRITERIA = "RECENT"
const val DEFAULT_IS_INTERESTED = false
const val DEFAULT_NOVEL_RATING = 0.0f

data class LibraryFilter(
    val sortCriteria: String = DEFAULT_SORT_CRITERIA,
    val isInterested: Boolean = DEFAULT_IS_INTERESTED,
    val novelRating: Float = DEFAULT_NOVEL_RATING,
    val readStatuses: List<String> = emptyList(),
    val attractivePoints: List<String> = emptyList(),
)
