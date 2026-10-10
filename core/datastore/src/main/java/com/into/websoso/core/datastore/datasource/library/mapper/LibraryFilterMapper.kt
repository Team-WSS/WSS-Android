package com.into.websoso.core.datastore.datasource.library.mapper

import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.data.filter.model.DEFAULT_SORT_CRITERIA
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

// 1.8.x 이하는 정렬 기준을 enum 이름("RECENT", "OLD")으로 저장했다.
// 서버 v2 명세 key로 마이그레이션하고, 알 수 없는 값은 기본값으로 대체한다.
private val LEGACY_SORT_CRITERIA_MIGRATION = mapOf(
    "RECENT" to "created_desc",
    "OLD" to "created_asc",
)

private val VALID_SORT_CRITERIA_KEYS = setOf(
    "created_desc",
    "created_asc",
    "title",
    "read_date",
    "rating_desc",
    "rating_asc",
)

private fun String.toMigratedSortCriteria(): String =
    LEGACY_SORT_CRITERIA_MIGRATION[this]
        ?: takeIf { it in VALID_SORT_CRITERIA_KEYS }
        ?: DEFAULT_SORT_CRITERIA

internal fun LibraryFilterPreferences.toData(): LibraryFilter =
    LibraryFilter(
        sortCriteria = sortCriteria.toMigratedSortCriteria(),
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
