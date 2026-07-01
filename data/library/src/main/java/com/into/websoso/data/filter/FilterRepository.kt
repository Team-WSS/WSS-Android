package com.into.websoso.data.filter

import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow

interface FilterRepository {
    val filterFlow: Flow<LibraryFilter>

    suspend fun updateFilter(
        isInterested: Boolean? = null,
        sortCriteria: String? = null,
    )

    suspend fun applyLibraryFilter(
        readStatuses: List<String>,
        attractivePoints: List<String>,
        genres: List<String>,
        isComplete: Boolean?,
        ratingMin: Float,
        ratingMax: Float,
        isRatingless: Boolean,
        keywords: List<String>,
    )
}
