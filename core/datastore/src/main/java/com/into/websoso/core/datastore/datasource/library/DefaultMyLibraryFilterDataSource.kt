package com.into.websoso.core.datastore.datasource.library

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.datastore.datasource.library.model.LibraryFilterPreferences
import com.into.websoso.core.datastore.di.MyLibraryFilterDataStore
import com.into.websoso.data.library.datasource.MyLibraryFilterLocalDataSource
import com.into.websoso.data.library.model.LibraryFilterParams
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

internal class DefaultMyLibraryFilterDataSource
    @Inject
    constructor(
        @MyLibraryFilterDataStore private val myLibraryFilterDataStore: DataStore<Preferences>,
        @Dispatcher(WebsosoDispatchers.DEFAULT) private val dispatcher: CoroutineDispatcher,
    ) : MyLibraryFilterLocalDataSource {
        override val myLibraryFilterFlow: Flow<LibraryFilterParams?>
            get() = myLibraryFilterDataStore.data
                .map { prefs ->
                    prefs[LIBRARY_FILTER_PARAMS_KEY]?.let { jsonString ->
                        withContext(dispatcher) {
                            Json.decodeFromString<LibraryFilterPreferences>(jsonString).toData()
                        }
                    }
                }.distinctUntilChanged()

        override suspend fun updateMyLibraryFilter(
            readStatuses: Map<String, Boolean>,
            attractivePoints: Map<String, Boolean>,
            novelRating: Float,
        ) {
            myLibraryFilterDataStore.edit { prefs ->
                prefs[LIBRARY_FILTER_PARAMS_KEY] =
                    withContext(dispatcher) {
                        Json.encodeToString(
                            LibraryFilterPreferences(
                                readStatuses = readStatuses,
                                attractivePoints = attractivePoints,
                                novelRating = novelRating,
                            ),
                        )
                    }
            }
        }

        override suspend fun deleteMyLibraryFilter() {
            myLibraryFilterDataStore.edit { prefs ->
                prefs.remove(LIBRARY_FILTER_PARAMS_KEY)
            }
        }

        companion object {
            private val LIBRARY_FILTER_PARAMS_KEY = stringPreferencesKey("LIBRARY_FILTER_PARAMS_KEY")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface MyLibraryFilterDataSourceModule {
    @Binds
    @Singleton
    fun bindMyLibraryFilterLocalDataSource(
        defaultMyLibraryFilterDataSource: DefaultMyLibraryFilterDataSource,
    ): MyLibraryFilterLocalDataSource
}
