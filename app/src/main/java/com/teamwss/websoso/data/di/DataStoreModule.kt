package com.teamwss.websoso.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private fun Context.createDataStore(preferencesName: String): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(this, preferencesName)),
            produceFile = { this.preferencesDataStoreFile(preferencesName) }
        )
    }

    @Singleton
    @Provides
    @Named("userPreferencesDataStore")
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore(USER_PREFERENCES)
    }

    private const val USER_PREFERENCES = "com.teamwss.websoso.user_preferences"
}
