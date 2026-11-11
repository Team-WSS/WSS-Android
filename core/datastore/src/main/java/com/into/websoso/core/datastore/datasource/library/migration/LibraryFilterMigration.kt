package com.into.websoso.core.datastore.datasource.library.migration

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.datastore.migration.VersionedPreferencesMigration
import com.into.websoso.data.filter.model.DEFAULT_IS_RATINGLESS
import com.into.websoso.data.filter.model.DEFAULT_RATING_MAX
import com.into.websoso.data.filter.model.DEFAULT_RATING_MIN
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject

internal val LIBRARY_FILTER_PARAMS_KEY = stringPreferencesKey("LIBRARY_FILTER_PARAMS_KEY")

private const val LIBRARY_FILTER_SCHEMA_VERSION = 1

internal val libraryFilterMigration = VersionedPreferencesMigration(
    currentVersion = LIBRARY_FILTER_SCHEMA_VERSION,
    steps = mapOf(
        0 to MutablePreferences::migrateLibraryFilterFromLegacy,
    ),
)

private const val SORT_CRITERIA_KEY = "sortCriteria"
private const val LEGACY_NOVEL_RATING_KEY = "novelRating"
private const val RATING_MIN_KEY = "ratingMin"
private const val RATING_MAX_KEY = "ratingMax"
private const val IS_RATINGLESS_KEY = "isRatingless"

// 1.8.x 이하는 정렬 기준을 enum 이름("RECENT", "OLD")으로 저장했다.
private val LEGACY_SORT_CRITERIA = mapOf(
    "RECENT" to "created_desc",
    "OLD" to "created_asc",
)

/**
 * 0 → 1: 버전 관리 도입 이전 서재 필터를 서버 v2 명세로 변환한다.
 * - 정렬 기준 enum 이름을 서버 key로 변환한다.
 * - 단일 별점(novelRating)을 별점 범위(ratingMin, ratingMax)로 변환한다.
 * - JSON으로 읽을 수 없는 값은 폐기한다.
 */
private fun MutablePreferences.migrateLibraryFilterFromLegacy() {
    val jsonString = this[LIBRARY_FILTER_PARAMS_KEY] ?: return
    val root = runCatching { Json.parseToJsonElement(jsonString).jsonObject }.getOrNull()
    if (root == null) {
        remove(LIBRARY_FILTER_PARAMS_KEY)
        return
    }

    this[LIBRARY_FILTER_PARAMS_KEY] = root
        .migrateLegacySortCriteria()
        .migrateLegacyRating()
        .toString()
}

private fun JsonObject.migrateLegacySortCriteria(): JsonObject {
    val sortCriteria = (this[SORT_CRITERIA_KEY] as? JsonPrimitive)?.contentOrNull
    val migrated = LEGACY_SORT_CRITERIA[sortCriteria] ?: return this

    return JsonObject(this + (SORT_CRITERIA_KEY to JsonPrimitive(migrated)))
}

private fun JsonObject.migrateLegacyRating(): JsonObject {
    val legacyRating = (this[LEGACY_NOVEL_RATING_KEY] as? JsonPrimitive)?.floatOrNull
    val hasNewRating = containsKey(RATING_MIN_KEY) || containsKey(RATING_MAX_KEY)
    if (hasNewRating || legacyRating == null) return JsonObject(this - LEGACY_NOVEL_RATING_KEY)

    return JsonObject(
        this - LEGACY_NOVEL_RATING_KEY +
            mapOf(
                RATING_MIN_KEY to JsonPrimitive(legacyRating.coerceIn(DEFAULT_RATING_MIN, DEFAULT_RATING_MAX)),
                RATING_MAX_KEY to JsonPrimitive(DEFAULT_RATING_MAX),
                IS_RATINGLESS_KEY to JsonPrimitive(DEFAULT_IS_RATINGLESS),
            ),
    )
}
