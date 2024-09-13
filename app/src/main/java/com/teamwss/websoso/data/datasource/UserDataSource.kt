package com.teamwss.websoso.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    object PreferencesKey {
        val ACCESS_TOKEN_KEY = booleanPreferencesKey("NOVEL_DETAIL_FIRST_LAUNCHED")
    }

    suspend fun saveNovelDetailFirstLaunched(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCESS_TOKEN_KEY] = value
        }
    }

    suspend fun fetchNovelDetailFirstLaunched(): Boolean {
        return dataStore.data.first()[PreferencesKey.ACCESS_TOKEN_KEY] ?: true
    }
}
