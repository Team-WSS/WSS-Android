package com.into.websoso.core.datastore.datasource.library.model

import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryFilterPreferences(
    val sortCriteria: String,
    val isInterested: Boolean,
    val readStatuses: List<String>,
    val attractivePoints: List<String>,
    val genres: List<String> = emptyList(),
    val isComplete: Boolean? = null,
    val ratingMin: Float = 0.0f,
    val ratingMax: Float = 5.0f,
    val isRatingless: Boolean = false,
    val keywords: List<String> = emptyList(),
)
