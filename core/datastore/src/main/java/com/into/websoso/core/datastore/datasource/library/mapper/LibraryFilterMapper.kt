package com.into.websoso.core.datastore.datasource.library.mapper

import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.data.library.model.LibraryFilterParams

internal fun LibraryFilterParams.toPreferences(): LibraryFilterPreferences =
    LibraryFilterPreferences(
        sortCriteria = sortCriteria,
        isInterest = isInterest,
        readStatuses = readStatuses,
        attractivePoints = attractivePoints,
        novelRating = novelRating,
    )

internal fun LibraryFilterPreferences.toData(): LibraryFilterParams =
    LibraryFilterParams(
        sortCriteria = sortCriteria,
        isInterest = isInterest,
        readStatuses = readStatuses,
        attractivePoints = attractivePoints,
        novelRating = novelRating,
    )
