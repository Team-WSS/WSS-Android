package com.teamwss.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    suspend fun saveUserPreferences(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun fetchUserPreferences(key: Preferences.Key<Boolean>): Boolean {
        return dataStore.data.first()[key] ?: true
    }

    companion object {
        val KEY_NOVEL_DETAIL_FIRST_LAUNCHED = booleanPreferencesKey("novel_detail_first_launched")
    }
}
