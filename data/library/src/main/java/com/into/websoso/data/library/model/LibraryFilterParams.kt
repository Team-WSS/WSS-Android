package com.into.websoso.data.library.model

data class LibraryFilterParams(
    val sortCriteria: String = "",
    val isInterest: Boolean? = null,
    val readStatuses: Map<String, Boolean> = mapOf(),
    val attractivePoints: Map<String, Boolean> = mapOf(),
    val novelRating: Float? = null,
)
