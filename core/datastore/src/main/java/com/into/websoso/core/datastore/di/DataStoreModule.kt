package com.into.websoso.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
    private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = ACCOUNT_DATASTORE)

    private const val MY_LIBRARY_FILTER_DATASTORE = "MY_LIBRARY_FILTER_DATASTORE"
    private val Context.myLibraryFilterDataStore: DataStore<Preferences> by preferencesDataStore(
        name = MY_LIBRARY_FILTER_DATASTORE,
    )

    @Provides
    @Singleton
    @AccountDataStore
    internal fun provideAccountPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.accountDataStore

    @Provides
    @Singleton
    @MyLibraryFilterDataStore
    internal fun provideMyLibraryFilterPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.myLibraryFilterDataStore
}
