package com.into.websoso.core.datastore.datasource.account

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.datastore.di.AccountDataStore
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultAccountDataSource
    @Inject
    constructor(
        @AccountDataStore private val accountDataStore: DataStore<Preferences>,
    ) : AccountLocalDataSource {
        override suspend fun accessToken(): String =
            accountDataStore.data
                .map { preferences ->
                    preferences[ACCESS_TOKEN].orEmpty()
                }.first()

        override suspend fun refreshToken(): String =
            accountDataStore.data
                .map { preferences ->
                    preferences[REFRESH_TOKEN].orEmpty()
                }.first()

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

        companion object {
            private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
            private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface AccountDataSourceModule {
    @Binds
    @Singleton
    fun bindAccountLocalDataSource(defaultAccountDataSource: DefaultAccountDataSource): AccountLocalDataSource
}
