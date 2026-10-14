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

// 저장값은 여러 버전의 앱이 남긴 값이므로 서버 명세에 없는 정렬 기준은 기본값으로 대체한다.
// 구버전 값 변환은 LibraryFilterMigration에서 처리한다.
private val VALID_SORT_CRITERIA_KEYS = setOf(
    "created_desc",
    "created_asc",
    "title",
    "read_date",
    "rating_desc",
    "rating_asc",
)

private fun String.toValidSortCriteria(): String = takeIf { it in VALID_SORT_CRITERIA_KEYS } ?: DEFAULT_SORT_CRITERIA

internal fun LibraryFilterPreferences.toData(): LibraryFilter =
    LibraryFilter(
        sortCriteria = sortCriteria.toValidSortCriteria(),
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
