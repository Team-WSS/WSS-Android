package com.into.websoso.core.datastore.datasource.library.model

import com.into.websoso.data.library.model.LibraryFilterParams
import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryFilterPreferences(
    val sortCriteria: String,
    val isInterested: Boolean,
    val readStatuses: Map<String, Boolean>,
    val attractivePoints: Map<String, Boolean>,
    val novelRating: Float,
) {
    internal fun toData(): LibraryFilterParams =
        LibraryFilterParams(
            sortCriteria = sortCriteria,
            isInterested = isInterested,
            readStatuses = readStatuses,
            attractivePoints = attractivePoints,
            novelRating = novelRating,
        )
}

internal fun LibraryFilterParams.toPreferences(): LibraryFilterPreferences =
    LibraryFilterPreferences(
        sortCriteria = sortCriteria,
        isInterested = isInterested,
        readStatuses = readStatuses,
        attractivePoints = attractivePoints,
        novelRating = novelRating,
    )
