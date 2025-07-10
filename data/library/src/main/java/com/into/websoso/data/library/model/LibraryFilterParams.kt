package com.into.websoso.data.library.model

data class LibraryFilterParams(
    val lastUserNovelId: Long = 184,
    val size: Int = 10,
    val sortCriteria: String = "",
    val isInterest: Boolean? = null,
    val readStatuses: List<String>? = null,
    val attractivePoints: List<String>? = null,
    val novelRating: Float? = null,
    val query: String? = null, // 검색어
    val updatedSince: String? = null,
)
