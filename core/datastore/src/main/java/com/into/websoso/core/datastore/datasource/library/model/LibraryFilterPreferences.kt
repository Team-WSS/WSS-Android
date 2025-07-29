package com.into.websoso.core.datastore.datasource.library.model

import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryFilterPreferences(
    val sortCriteria: String,
    val isInterested: Boolean,
    val readStatuses: Map<String, Boolean>,
    val attractivePoints: Map<String, Boolean>,
    val novelRating: Float,
)
