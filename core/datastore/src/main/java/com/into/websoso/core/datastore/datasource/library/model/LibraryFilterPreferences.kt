package com.into.websoso.core.datastore.datasource.library.model

import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryFilterPreferences(
    val sortCriteria: String,
    val isInterested: Boolean,
    val readStatuses: List<String>,
    val attractivePoints: List<String>,
    val novelRating: Float,
)
