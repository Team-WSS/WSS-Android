package com.into.websoso.data.filter

import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow

interface FilterRepository {
    val filterFlow: Flow<LibraryFilter>

    suspend fun updateFilter(
        readStatuses: List<String>? = null,
        attractivePoints: List<String>? = null,
        novelRating: Float? = null,
        isInterested: Boolean? = null,
        sortCriteria: String? = null,
    )
}
