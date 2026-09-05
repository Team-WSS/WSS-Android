package com.into.websoso.core.datastore.datasource.feed

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.datastore.datasource.feed.model.PendingFeedLikePreferences
import com.into.websoso.core.datastore.datasource.feed.model.PendingFeedLikesPreferences
import com.into.websoso.core.datastore.di.PendingFeedLikeDataStore
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultPendingFeedLikeStore
    @Inject
    constructor(
        @param:PendingFeedLikeDataStore private val pendingFeedLikeDataStore: DataStore<Preferences>,
        @param:Dispatcher(WebsosoDispatchers.DEFAULT) private val dispatcher: CoroutineDispatcher,
    ) : PendingFeedLikeStore {
        override val pendingLikes: Flow<Map<Long, Boolean>>
            get() = pendingFeedLikeDataStore.data
                .map { preferences ->
                    withContext(dispatcher) {
                        decodePendingLikes(preferences[PENDING_FEED_LIKES_KEY])
                    }
                }.distinctUntilChanged()

        override suspend fun getPendingLikes(): Map<Long, Boolean> = pendingLikes.first()

        override suspend fun updatePendingLike(
            feedId: Long,
            isLiked: Boolean,
        ) {
            pendingFeedLikeDataStore.edit { preferences ->
                val pendingLikes: MutableMap<Long, Boolean> = decodePendingLikes(
                    preferences[PENDING_FEED_LIKES_KEY],
                ).toMutableMap()
                pendingLikes[feedId] = isLiked
                preferences[PENDING_FEED_LIKES_KEY] = encodePendingLikes(pendingLikes)
            }
        }

        override suspend fun deletePendingLike(feedId: Long) {
            pendingFeedLikeDataStore.edit { preferences ->
                val pendingLikes: MutableMap<Long, Boolean> = decodePendingLikes(
                    preferences[PENDING_FEED_LIKES_KEY],
                ).toMutableMap()

                pendingLikes.remove(feedId)
                if (pendingLikes.isEmpty()) {
                    preferences.remove(PENDING_FEED_LIKES_KEY)
                } else {
                    preferences[PENDING_FEED_LIKES_KEY] = encodePendingLikes(pendingLikes)
                }
            }
        }

        override suspend fun deletePendingLikeIfMatched(
            feedId: Long,
            isLiked: Boolean,
        ): Boolean {
            var deleted = false

            pendingFeedLikeDataStore.edit { preferences ->
                val pendingLikes: MutableMap<Long, Boolean> = decodePendingLikes(
                    preferences[PENDING_FEED_LIKES_KEY],
                ).toMutableMap()
                if (pendingLikes[feedId] != isLiked) return@edit

                pendingLikes.remove(feedId)
                if (pendingLikes.isEmpty()) {
                    preferences.remove(PENDING_FEED_LIKES_KEY)
                } else {
                    preferences[PENDING_FEED_LIKES_KEY] = encodePendingLikes(pendingLikes)
                }
                deleted = true
            }

            return deleted
        }

        private fun decodePendingLikes(jsonString: String?): Map<Long, Boolean> {
            if (jsonString == null) return emptyMap()

            return runCatching {
                Json
                    .decodeFromString<PendingFeedLikesPreferences>(jsonString)
                    .likes
                    .associate { pendingLike -> pendingLike.feedId to pendingLike.isLiked }
            }.getOrDefault(emptyMap())
        }

        private fun encodePendingLikes(pendingLikes: Map<Long, Boolean>): String =
            Json.encodeToString(
                PendingFeedLikesPreferences(
                    likes = pendingLikes.entries.map { pendingLike ->
                        PendingFeedLikePreferences(
                            feedId = pendingLike.key,
                            isLiked = pendingLike.value,
                        )
                    },
                ),
            )

        companion object {
            private val PENDING_FEED_LIKES_KEY = stringPreferencesKey("PENDING_FEED_LIKES_KEY")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface PendingFeedLikeStoreModule {
    @Binds
    @Singleton
    fun bindPendingFeedLikeStore(defaultPendingFeedLikeStore: DefaultPendingFeedLikeStore): PendingFeedLikeStore
}
