package com.into.websoso.core.datastore.migration

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.datastore.migration.VersionedPreferencesMigration.Companion.SCHEMA_VERSION
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionedPreferencesMigrationTest {
    private val history = stringPreferencesKey("history")

    private val migration = VersionedPreferencesMigration(
        currentVersion = 3,
        steps = mapOf(
            0 to { this[history] = this[history].orEmpty() + "0" },
            1 to { this[history] = this[history].orEmpty() + "1" },
            2 to { this[history] = this[history].orEmpty() + "2" },
        ),
    )

    private suspend fun migrateIfNeeded(preferences: Preferences): Preferences =
        if (migration.shouldMigrate(preferences)) migration.migrate(preferences) else preferences

    @Test
    fun `버전 키가 없으면 모든 단계를 순서대로 적용하고 현재 버전을 기록한다`() =
        runTest {
            val migrated = migrateIfNeeded(preferencesOf())

            assertEquals("012", migrated[history])
            assertEquals(3, migrated[SCHEMA_VERSION])
        }

    @Test
    fun `저장된 버전 이후의 단계만 적용한다`() =
        runTest {
            val migrated = migrateIfNeeded(preferencesOf(SCHEMA_VERSION to 1))

            assertEquals("12", migrated[history])
            assertEquals(3, migrated[SCHEMA_VERSION])
        }

    @Test
    fun `저장된 버전이 현재 버전 이상이면 마이그레이션하지 않는다`() =
        runTest {
            assertFalse(migration.shouldMigrate(preferencesOf(SCHEMA_VERSION to 3)))
            assertFalse(migration.shouldMigrate(preferencesOf(SCHEMA_VERSION to 4)))
            assertTrue(migration.shouldMigrate(preferencesOf(SCHEMA_VERSION to 2)))
        }

    @Test
    fun `마이그레이션은 한 번만 적용된다`() =
        runTest {
            val once = migrateIfNeeded(preferencesOf())
            val twice = migrateIfNeeded(once)

            assertEquals(once, twice)
        }

    @Test
    fun `현재 버전 범위를 벗어난 단계는 등록할 수 없다`() {
        assertThrows(IllegalArgumentException::class.java) {
            VersionedPreferencesMigration(currentVersion = 1, steps = mapOf(1 to {}))
        }
        assertThrows(IllegalArgumentException::class.java) {
            VersionedPreferencesMigration(currentVersion = 0)
        }
    }
}
