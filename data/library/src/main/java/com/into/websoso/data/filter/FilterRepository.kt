package com.into.websoso.data.filter

import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow

interface FilterRepository {
    val filterFlow: Flow<LibraryFilter>

    suspend fun updateFilter(
        readStatuses: Map<String, Boolean>? = null,
        attractivePoints: Map<String, Boolean>? = null,
        novelRating: Float? = null,
        isInterested: Boolean? = null,
        sortCriteria: String? = null,
    )
}
