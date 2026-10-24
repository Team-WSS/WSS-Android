package com.into.websoso.core.datastore.datasource.library.migration

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import com.into.websoso.core.datastore.datasource.library.mapper.toData
import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.core.datastore.migration.VersionedPreferencesMigration.Companion.SCHEMA_VERSION
import com.into.websoso.data.filter.model.LibraryFilter
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class LibraryFilterMigrationTest {
    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun migrate(stored: String?): Preferences {
        val preferences = stored?.let { preferencesOf(LIBRARY_FILTER_PARAMS_KEY to it) } ?: preferencesOf()
        return libraryFilterMigration.migrate(preferences)
    }

    private fun Preferences.toLibraryFilter(): LibraryFilter =
        json.decodeFromString<LibraryFilterPreferences>(this[LIBRARY_FILTER_PARAMS_KEY]!!).toData()

    private fun storedFilter(
        sortCriteria: String,
        extra: String = "",
    ): String = """{"sortCriteria":"$sortCriteria","isInterested":false,"readStatuses":[],"attractivePoints":[]$extra}"""

    @Test
    fun `구버전 정렬 기준을 서버 명세 key로 변환해 저장한다`() =
        runTest {
            mapOf(
                "RECENT" to "created_desc",
                "OLD" to "created_asc",
            ).forEach { (stored, expected) ->
                assertEquals(expected, migrate(storedFilter(stored)).toLibraryFilter().sortCriteria)
            }
        }

    @Test
    fun `핫픽스 이후 저장된 새 정렬 기준은 버전 키가 없어도 그대로 유지한다`() =
        runTest {
            listOf("created_desc", "created_asc", "title", "read_date", "rating_desc", "rating_asc").forEach { stored ->
                val migrated = migrate(storedFilter(stored))

                assertEquals(stored, migrated.toLibraryFilter().sortCriteria)
                assertEquals(1, migrated[SCHEMA_VERSION])
            }
        }

    @Test
    fun `단일 별점을 별점 범위로 변환하고 구 키를 제거한다`() =
        runTest {
            val migrated = migrate(storedFilter("RECENT", extra = ""","novelRating":3.5"""))

            val filter = migrated.toLibraryFilter()
            assertEquals(3.5f, filter.ratingMin)
            assertEquals(5.0f, filter.ratingMax)
            assertFalse(filter.isRatingless)
            assertFalse(migrated[LIBRARY_FILTER_PARAMS_KEY]!!.contains("novelRating"))
        }

    @Test
    fun `별점 범위가 이미 있으면 단일 별점을 무시한다`() =
        runTest {
            val migrated = migrate(storedFilter("created_desc", extra = ""","novelRating":3.5,"ratingMin":1.0,"ratingMax":4.0"""))

            val filter = migrated.toLibraryFilter()
            assertEquals(1.0f, filter.ratingMin)
            assertEquals(4.0f, filter.ratingMax)
        }

    @Test
    fun `JSON으로 읽을 수 없는 값은 폐기한다`() =
        runTest {
            val migrated = migrate("not json")

            assertNull(migrated[LIBRARY_FILTER_PARAMS_KEY])
            assertEquals(1, migrated[SCHEMA_VERSION])
        }

    @Test
    fun `저장된 필터가 없으면 버전만 기록한다`() =
        runTest {
            val migrated = migrate(null)

            assertNull(migrated[LIBRARY_FILTER_PARAMS_KEY])
            assertEquals(1, migrated[SCHEMA_VERSION])
        }
}
