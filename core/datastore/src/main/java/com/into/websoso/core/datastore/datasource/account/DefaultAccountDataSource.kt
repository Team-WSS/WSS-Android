package com.into.websoso.core.datastore.datasource.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.datastore.di.AccountDataStore
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultAccountDataSource
    @Inject
    constructor(
        @AccountDataStore private val accountDataStore: DataStore<Preferences>,
    ) : AccountLocalDataSource {
        override suspend fun accessToken(): String =
            accountDataStore.data
                .map { preferences ->
                    preferences[ACCESS_TOKEN].orEmpty()
                }.last()

        override suspend fun refreshToken(): String =
            accountDataStore.data
                .map { preferences ->
                    preferences[REFRESH_TOKEN].orEmpty()
                }.last()

        override suspend fun isAutoLogin(): Boolean =
            accountDataStore.data
                .map { preferences ->
                    preferences[IS_LOGIN] ?: false
                }.last()

        override suspend fun saveAccessToken(accessToken: String) {
            accountDataStore.edit { preferences ->
                preferences[ACCESS_TOKEN] = accessToken
            }
        }

        override suspend fun saveRefreshToken(refreshToken: String) {
            accountDataStore.edit { preferences ->
                preferences[REFRESH_TOKEN] = refreshToken
            }
        }

        override suspend fun saveIsAutoLogin(isAutoLogin: Boolean) {
            accountDataStore.edit { preferences ->
                preferences[IS_LOGIN] = isAutoLogin
            }
        }

        companion object {
            private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
            private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
            private val IS_LOGIN = booleanPreferencesKey("IS_LOGIN")
        }
    }
