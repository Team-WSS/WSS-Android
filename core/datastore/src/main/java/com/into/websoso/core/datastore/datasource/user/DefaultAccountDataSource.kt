package com.into.websoso.core.datastore.datasource.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.into.websoso.core.datastore.di.UserDataStore
import com.into.websoso.user.datasource.UserLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultUserDataSource
    @Inject
    constructor(
        @UserDataStore private val userDataStorage: DataStore<Preferences>,
    ) : UserLocalDataSource {
        override suspend fun getUserId(): Long {
            val preferences = userDataStorage.data.first()
            return preferences[USER_ID] ?: -1L
        }

        override suspend fun updateUserId(userId: Long) {
            userDataStorage.edit { preferences ->
                preferences[USER_ID] = userId
            }
        }

        companion object {
            private val USER_ID = longPreferencesKey("USERID")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface UserDataStorage {
    @Binds
    @Singleton
    fun bindUserLocalDataSource(defaultUserDataSource: DefaultUserDataSource): DefaultUserDataSource
}
