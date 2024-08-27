package com.teamwss.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPreferenceStorage: DataStore<Preferences>,
) {

    suspend fun saveNovelDetailFirstLaunched(value: Boolean) {
        userPreferenceStorage.edit { preferences ->
            preferences[KEY_NOVEL_DETAIL_FIRST_LAUNCHED] = value
        }
    }

    suspend fun fetchNovelDetailFirstLaunched(): Boolean {
        return userPreferenceStorage.data.first()[KEY_NOVEL_DETAIL_FIRST_LAUNCHED] ?: true
    }

    companion object {
        val KEY_NOVEL_DETAIL_FIRST_LAUNCHED = booleanPreferencesKey("novel_detail_first_launched")
    }
}
