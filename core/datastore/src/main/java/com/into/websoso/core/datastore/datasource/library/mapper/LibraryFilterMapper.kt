package com.into.websoso.core.datastore.datasource.library.mapper

import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.data.filter.model.LibraryFilter

internal fun LibraryFilter.toPreferences(): LibraryFilterPreferences =
    LibraryFilterPreferences(
        sortCriteria = sortCriteria,
        isInterested = isInterested,
        readStatuses = readStatuses,
        attractivePoints = attractivePoints,
        genres = genres,
        isComplete = isComplete,
        ratingMin = ratingMin,
        ratingMax = ratingMax,
        isRatingless = isRatingless,
        keywords = keywords,
    )

internal fun LibraryFilterPreferences.toData(): LibraryFilter =
    LibraryFilter(
        sortCriteria = sortCriteria,
        isInterested = isInterested,
        readStatuses = readStatuses,
        attractivePoints = attractivePoints,
        genres = genres,
        isComplete = isComplete,
        ratingMin = ratingMin,
        ratingMax = ratingMax,
        isRatingless = isRatingless,
        keywords = keywords,
    )
