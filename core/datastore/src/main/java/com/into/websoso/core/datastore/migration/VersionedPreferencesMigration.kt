package com.into.websoso.core.datastore.migration

import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

/**
 * 저장소에 스키마 버전을 함께 기록하고, 저장된 버전이 [currentVersion]보다 낮으면 단계별로 변환한다.
 *
 * 앱은 배포 시 전부 교체되지만 저장된 값은 이전 버전 앱이 남긴 그대로 남는다.
 * 저장 명세(키 이름, 값 형식)를 바꿀 때는 [currentVersion]을 올리고 이전 버전에서 넘어오는 단계를 [steps]에 추가한다.
 *
 * - 버전 키가 없으면 버전 관리 도입 이전([LEGACY_SCHEMA_VERSION])으로 본다.
 * - [steps]의 키는 변환 전 버전이며, 해당 단계는 `버전 → 버전 + 1` 변환을 수행한다.
 * - 신규 설치에서도 모든 단계가 빈 저장소를 대상으로 실행되므로, 각 단계는 값이 없는 경우를 처리해야 한다.
 */
class VersionedPreferencesMigration(
    private val currentVersion: Int,
    private val steps: Map<Int, MutablePreferences.() -> Unit> = emptyMap(),
) : DataMigration<Preferences> {
    init {
        require(currentVersion > LEGACY_SCHEMA_VERSION)
        require(steps.keys.all { it in LEGACY_SCHEMA_VERSION until currentVersion })
    }

    override suspend fun shouldMigrate(currentData: Preferences): Boolean = currentData.schemaVersion < currentVersion

    override suspend fun migrate(currentData: Preferences): Preferences =
        currentData.toMutablePreferences().apply {
            (currentData.schemaVersion until currentVersion).forEach { version ->
                steps[version]?.invoke(this)
            }
            this[SCHEMA_VERSION] = currentVersion
        }

    override suspend fun cleanUp() = Unit

    private val Preferences.schemaVersion: Int
        get() = this[SCHEMA_VERSION] ?: LEGACY_SCHEMA_VERSION

    companion object {
        internal val SCHEMA_VERSION = intPreferencesKey("SCHEMA_VERSION")
        internal const val LEGACY_SCHEMA_VERSION = 0
    }
}
