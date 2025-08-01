package com.into.websoso.data.filter.repository

import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.datasource.LibraryFilterLocalDataSource
import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyLibraryFilterRepository
    @Inject
    constructor(
        private val myLibraryFilterLocalDataSource: LibraryFilterLocalDataSource,
    ) : FilterRepository {
        override val filterFlow: Flow<LibraryFilter> =
            myLibraryFilterLocalDataSource.libraryFilterFlow
                .map { it ?: LibraryFilter() }
                .distinctUntilChanged()

        suspend fun deleteLibraryFilter() {
            myLibraryFilterLocalDataSource.deleteLibraryFilter()
        }

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

            myLibraryFilterLocalDataSource.updateLibraryFilter(libraryFilter = updatedFilter)
        }
    }
