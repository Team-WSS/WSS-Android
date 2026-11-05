package com.into.websoso.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.into.websoso.core.datastore.datasource.feed.DefaultPendingFeedLikeStore
import com.into.websoso.core.datastore.datasource.library.migration.libraryFilterMigration
import com.into.websoso.core.datastore.migration.VersionedPreferencesMigration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    private const val ACCOUNT_DATASTORE = "ACCOUNT_DATASTORE"
    private const val ACCOUNT_SCHEMA_VERSION = 1
    private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(
        name = ACCOUNT_DATASTORE,
        produceMigrations = { listOf(VersionedPreferencesMigration(currentVersion = ACCOUNT_SCHEMA_VERSION)) },
    )

    private const val LIBRARY_FILTER_DATASTORE = "LIBRARY_FILTER_DATASTORE"
    private val Context.libraryFilterDataStore: DataStore<Preferences> by preferencesDataStore(
        name = LIBRARY_FILTER_DATASTORE,
        produceMigrations = { listOf(libraryFilterMigration) },
    )

    private const val USER_DATASTORE = "USER_DATASTORE"
    private const val USER_SCHEMA_VERSION = 1
    private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_DATASTORE,
        produceMigrations = { listOf(VersionedPreferencesMigration(currentVersion = USER_SCHEMA_VERSION)) },
    )

    private const val PENDING_FEED_LIKE_DATASTORE = "PENDING_FEED_LIKE_DATASTORE"
    private const val PENDING_FEED_LIKE_SCHEMA_VERSION = 1
    private val Context.pendingFeedLikeDataStore: DataStore<Preferences> by preferencesDataStore(
        name = PENDING_FEED_LIKE_DATASTORE,
        corruptionHandler = DefaultPendingFeedLikeStore.corruptionHandler,
        produceMigrations = { listOf(VersionedPreferencesMigration(currentVersion = PENDING_FEED_LIKE_SCHEMA_VERSION)) },
    )

    @Provides
    @Singleton
    @AccountDataStore
    internal fun provideAccountPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.accountDataStore

    @Provides
    @Singleton
    @LibraryFilterDataStore
    internal fun provideLibraryFilterPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.libraryFilterDataStore

    @Provides
    @Singleton
    @UserDataStore
    fun provideUserDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.userDataStore

    @Provides
    @Singleton
    @PendingFeedLikeDataStore
    fun providePendingFeedLikeDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.pendingFeedLikeDataStore
}
