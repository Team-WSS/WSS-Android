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
            isInterested: Boolean?,
            sortCriteria: String?,
        ) {
            val savedFilter = filterFlow.first()
            val updatedFilter = savedFilter.copy(
                sortCriteria = sortCriteria ?: savedFilter.sortCriteria,
                isInterested = isInterested ?: savedFilter.isInterested,
            )

            myLibraryFilterLocalDataSource.updateLibraryFilter(libraryFilter = updatedFilter)
        }

        override suspend fun applyLibraryFilter(
            readStatuses: List<String>,
            attractivePoints: List<String>,
            genres: List<String>,
            isComplete: Boolean?,
            ratingMin: Float,
            ratingMax: Float,
            isRatingless: Boolean,
            keywords: List<String>,
        ) {
            val savedFilter = filterFlow.first()
            val updatedFilter = savedFilter.copy(
                readStatuses = readStatuses,
                attractivePoints = attractivePoints,
                genres = genres,
                isComplete = isComplete,
                ratingMin = ratingMin,
                ratingMax = ratingMax,
                isRatingless = isRatingless,
                keywords = keywords,
            )

            myLibraryFilterLocalDataSource.updateLibraryFilter(libraryFilter = updatedFilter)
        }
    }
