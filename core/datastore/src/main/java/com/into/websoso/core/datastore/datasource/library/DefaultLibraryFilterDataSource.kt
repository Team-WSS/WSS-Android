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
import com.into.websoso.data.filter.model.DEFAULT_IS_RATINGLESS
import com.into.websoso.data.filter.model.DEFAULT_RATING_MAX
import com.into.websoso.data.filter.model.DEFAULT_RATING_MIN
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryFilterDataSource
    @Inject
    constructor(
        @LibraryFilterDataStore private val libraryFilterDataStore: DataStore<Preferences>,
        @Dispatcher(WebsosoDispatchers.DEFAULT) private val dispatcher: CoroutineDispatcher,
    ) : LibraryFilterLocalDataSource {
        private val json = Json { ignoreUnknownKeys = true }

        override val libraryFilterFlow: Flow<LibraryFilter?>
            get() = libraryFilterDataStore.data
                .map { prefs ->
                    prefs[LIBRARY_FILTER_PARAMS_KEY]?.let { jsonString ->
                        withContext(dispatcher) {
                            json
                                .decodeFromString<LibraryFilterPreferences>(jsonString)
                                .migrateLegacyRating(jsonString)
                                .toData()
                        }
                    }
                }.distinctUntilChanged()

        private fun LibraryFilterPreferences.migrateLegacyRating(jsonString: String): LibraryFilterPreferences {
            val root = json.parseToJsonElement(jsonString).jsonObject

            val hasNewRating = root.containsKey(RATING_MIN_KEY) || root.containsKey(RATING_MAX_KEY)
            val legacyRating = root[LEGACY_NOVEL_RATING_KEY]?.jsonPrimitive?.floatOrNull
            if (hasNewRating || legacyRating == null) return this

            return copy(
                ratingMin = legacyRating.coerceIn(DEFAULT_RATING_MIN, DEFAULT_RATING_MAX),
                ratingMax = DEFAULT_RATING_MAX,
                isRatingless = DEFAULT_IS_RATINGLESS,
            )
        }

        override suspend fun updateLibraryFilter(libraryFilter: LibraryFilter) {
            val encodedJsonString = withContext(dispatcher) {
                json.encodeToString(
                    libraryFilter.toPreferences(),
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
            private const val LEGACY_NOVEL_RATING_KEY = "novelRating"
            private const val RATING_MIN_KEY = "ratingMin"
            private const val RATING_MAX_KEY = "ratingMax"
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryFilterDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryFilterLocalDataSource(defaultLibraryFilterDataSource: DefaultLibraryFilterDataSource): LibraryFilterLocalDataSource
}
