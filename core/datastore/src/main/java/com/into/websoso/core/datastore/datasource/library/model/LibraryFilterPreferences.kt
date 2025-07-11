package com.into.websoso.core.datastore.datasource.library.model

import com.into.websoso.data.library.model.LibraryFilterParams
import kotlinx.serialization.Serializable

@Serializable
internal data class LibraryFilterPreferences(
    val sortCriteria: String = "",
    val isInterest: Boolean = false,
    val readStatuses: Map<String, Boolean>,
    val attractivePoints: Map<String, Boolean>,
    val novelRating: Float,
) {
    internal fun toData(): LibraryFilterParams =
        LibraryFilterParams(
            sortCriteria = sortCriteria,
            isInterest = isInterest,
            readStatuses = readStatuses,
            attractivePoints = attractivePoints,
            novelRating = novelRating,
        )
}
