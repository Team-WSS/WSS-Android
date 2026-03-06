package com.into.websoso.core.datastore.datasource.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.datastore.di.UserDataStore
import com.into.websoso.user.datasource.UserLocalDataSource
import com.into.websoso.user.model.UserInfoEntity
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
        override suspend fun getUserInfo(): UserInfoEntity {
            val preferences = userDataStorage.data.first()

            val userId = preferences[USER_ID] ?: -1L
            val nickname = preferences[USER_NICKNAME_KEY] ?: ""
            val gender = preferences[USER_GENDER_KEY] ?: ""

            return UserInfoEntity(
                userId = userId,
                nickname = nickname,
                gender = gender,
            )
        }

        override suspend fun updateUserInfo(
            userId: Long,
            nickname: String,
            gender: String,
        ) {
            userDataStorage.edit { preferences ->
                preferences[USER_ID] = userId
                preferences[USER_NICKNAME_KEY] = nickname
                preferences[USER_GENDER_KEY] = gender
            }
        }

        companion object {
            private val USER_ID = longPreferencesKey("USERID")
            private val USER_NICKNAME_KEY = stringPreferencesKey("USER_NICKNAME")
            private val USER_GENDER_KEY = stringPreferencesKey("USER_GENDER")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface UserDataStorage {
    @Binds
    @Singleton
    fun bindUserLocalDataSource(defaultUserDataSource: DefaultUserDataSource): UserLocalDataSource
}
