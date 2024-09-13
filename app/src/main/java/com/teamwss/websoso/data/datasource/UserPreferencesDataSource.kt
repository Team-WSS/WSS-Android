package com.teamwss.websoso.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class UserPreferencesDataSource @Inject constructor(
    @Named("userPreferencesDataStore") private val dataStore: DataStore<Preferences>,
) {

    object PreferencesKey {
        val NOVEL_DETAIL_FIRST_LAUNCHED_KEY = booleanPreferencesKey("NOVEL_DETAIL_FIRST_LAUNCHED")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("ACCESS_TOKEN")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN")
    }

    suspend fun saveNovelDetailFirstLaunched(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.NOVEL_DETAIL_FIRST_LAUNCHED_KEY] = value
        }
    }

    suspend fun fetchNovelDetailFirstLaunched(): Boolean {
        return dataStore.data.first()[PreferencesKey.NOVEL_DETAIL_FIRST_LAUNCHED_KEY] ?: true
    }

    suspend fun saveAccessToken(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.ACCESS_TOKEN_KEY] = value
        }
    }

    suspend fun fetchAccessToken(): String {
        return dataStore.data.first()[PreferencesKey.ACCESS_TOKEN_KEY] ?: ""
    }

    suspend fun saveRefreshToken(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.REFRESH_TOKEN_KEY] = value
        }
    }

    suspend fun fetchRefreshToken(): String {
        return dataStore.data.first()[PreferencesKey.REFRESH_TOKEN_KEY] ?: ""
    }
}
