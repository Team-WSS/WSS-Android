package com.into.websoso.data.library.model

data class LibraryFilterParams(
    val sortCriteria: String = DEFAULT_SORT_CRITERIA,
    val isInterested: Boolean = DEFAULT_IS_INTERESTED,
    val novelRating: Float = DEFAULT_NOVEL_RATING,
    val readStatuses: Map<String, Boolean> = mapOf(),
    val attractivePoints: Map<String, Boolean> = mapOf(),
)

private const val DEFAULT_SORT_CRITERIA = "RECENT"
private const val DEFAULT_IS_INTERESTED = false
private const val DEFAULT_NOVEL_RATING = 0.0f
