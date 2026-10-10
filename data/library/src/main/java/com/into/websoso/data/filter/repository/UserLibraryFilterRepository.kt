package com.into.websoso.data.filter.repository

import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal class UserLibraryFilterRepository
    @Inject
    constructor() : FilterRepository {
        private val _filterFlow = MutableStateFlow(LibraryFilter())
        override val filterFlow: Flow<LibraryFilter> = _filterFlow.asStateFlow()

        override suspend fun updateFilter(
            isInterested: Boolean?,
            sortCriteria: String?,
        ) {
            _filterFlow.update { current ->
                current.copy(
                    sortCriteria = sortCriteria ?: current.sortCriteria,
                    isInterested = isInterested ?: current.isInterested,
                )
            }
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
            _filterFlow.update { current ->
                current.copy(
                    readStatuses = readStatuses,
                    attractivePoints = attractivePoints,
                    genres = genres,
                    isComplete = isComplete,
                    ratingMin = ratingMin,
                    ratingMax = ratingMax,
                    isRatingless = isRatingless,
                    keywords = keywords,
                )
            }
        }
    }
