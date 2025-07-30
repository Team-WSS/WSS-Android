package com.into.websoso.core.datastore.datasource.library

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.datastore.datasource.library.mapper.toData
import com.into.websoso.core.datastore.datasource.library.mapper.toPreferences
import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.core.datastore.di.LibraryFilterDataStore
import com.into.websoso.data.filter.datasource.LibraryFilterLocalDataSource
import com.into.websoso.data.filter.model.LibraryFilter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultLibraryFilterDataSource
    @Inject
    constructor(
        @LibraryFilterDataStore private val libraryFilterDataStore: DataStore<Preferences>,
        @Dispatcher(WebsosoDispatchers.DEFAULT) private val dispatcher: CoroutineDispatcher,
    ) : LibraryFilterLocalDataSource {
        override val libraryFilterFlow: Flow<LibraryFilter?>
            get() = libraryFilterDataStore.data
                .map { prefs ->
                    prefs[LIBRARY_FILTER_PARAMS_KEY]?.let { jsonString ->
                        withContext(dispatcher) {
                            Json.decodeFromString<LibraryFilterPreferences>(jsonString).toData()
                        }
                    }
                }.distinctUntilChanged()

        override suspend fun updateLibraryFilter(params: LibraryFilter) {
            val encodedJsonString = withContext(dispatcher) {
                Json.encodeToString(
                    params.toPreferences(),
                )
            }

            libraryFilterDataStore.edit { prefs ->
                prefs[LIBRARY_FILTER_PARAMS_KEY] = encodedJsonString
            }
        }

        override suspend fun deleteLibraryFilter() {
            libraryFilterDataStore.edit { prefs ->
                prefs.remove(LIBRARY_FILTER_PARAMS_KEY)
            }
        }

        companion object {
            private val LIBRARY_FILTER_PARAMS_KEY = stringPreferencesKey("LIBRARY_FILTER_PARAMS_KEY")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryFilterDataSourceModule {
    @Binds
    fun bindLibraryFilterLocalDataSource(defaultLibraryFilterDataSource: DefaultLibraryFilterDataSource): LibraryFilterLocalDataSource
}
