package com.into.websoso.data.filter.repository

import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class UserLibraryFilterRepository
    @Inject
    constructor() : FilterRepository {
        private val _filterFlow = MutableStateFlow(LibraryFilter())
        override val filterFlow: Flow<LibraryFilter> = _filterFlow.asStateFlow()

        override suspend fun updateFilter(
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            isInterested: Boolean?,
            sortCriteria: String?,
        ) {
            val savedFilter = filterFlow.first()
            val updatedFilter = savedFilter.copy(
                sortCriteria = sortCriteria ?: savedFilter.sortCriteria,
                isInterested = isInterested ?: savedFilter.isInterested,
                readStatuses = readStatuses ?: savedFilter.readStatuses,
                attractivePoints = attractivePoints ?: savedFilter.attractivePoints,
                novelRating = novelRating ?: savedFilter.novelRating,
            )

            _filterFlow.update { updatedFilter }
        }
    }
