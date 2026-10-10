package com.into.websoso.core.datastore.datasource.feed

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
                        preferences.readPendingLikes()
                    }
                }.distinctUntilChanged()

        override suspend fun getPendingLikes(): Map<Long, Boolean> = pendingLikes.first()

        override suspend fun updatePendingLike(
            feedId: Long,
            isLiked: Boolean,
        ) {
            pendingFeedLikeDataStore.edit { preferences ->
                preferences.setAsideUnreadableLikes()
                val pendingLikes: MutableMap<Long, Boolean> = preferences.readPendingLikes().toMutableMap()
                pendingLikes[feedId] = isLiked
                preferences[PENDING_FEED_LIKES_KEY] = encodePendingLikes(pendingLikes)
            }
        }

        override suspend fun deletePendingLike(feedId: Long) {
            pendingFeedLikeDataStore.edit { preferences ->
                preferences.setAsideUnreadableLikes()
                val pendingLikes: MutableMap<Long, Boolean> = preferences.readPendingLikes().toMutableMap()

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
                preferences.setAsideUnreadableLikes()
                val pendingLikes: MutableMap<Long, Boolean> = preferences.readPendingLikes().toMutableMap()
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

        override suspend fun readResetNotice(): Boolean {
            var hasNotice = false
            pendingFeedLikeDataStore.edit { preferences ->
                preferences.setAsideUnreadableLikes()
                hasNotice = preferences[PENDING_FEED_LIKES_RESET_KEY] == true
            }
            return hasNotice
        }

        override suspend fun clearResetNotice() {
            pendingFeedLikeDataStore.edit { preferences ->
                preferences.remove(PENDING_FEED_LIKES_RESET_KEY)
            }
        }

        /** 해석할 수 없는 기록은 지우지 않고 보관함으로 옮긴 뒤, 복원하지 못했다는 표시를 남깁니다. */
        private fun MutablePreferences.setAsideUnreadableLikes() {
            val jsonString = this[PENDING_FEED_LIKES_KEY] ?: return
            if (decodePendingLikes(jsonString) != null) return
            this[PENDING_FEED_LIKES_UNREADABLE_KEY] = this[PENDING_FEED_LIKES_UNREADABLE_KEY].orEmpty() + jsonString
            remove(PENDING_FEED_LIKES_KEY)
            this[PENDING_FEED_LIKES_RESET_KEY] = true
        }

        private fun Preferences.readPendingLikes(): Map<Long, Boolean> =
            this[PENDING_FEED_LIKES_KEY]?.let { jsonString -> decodePendingLikes(jsonString) }.orEmpty()

        /** 해석하지 못하면 null을 반환해, 저장된 기록이 없는 경우와 구분합니다. */
        private fun decodePendingLikes(jsonString: String): Map<Long, Boolean>? =
            runCatching {
                Json
                    .decodeFromString<PendingFeedLikesPreferences>(jsonString)
                    .likes
                    .associate { pendingLike -> pendingLike.feedId to pendingLike.isLiked }
            }.getOrNull()

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
            private val PENDING_FEED_LIKES_RESET_KEY = booleanPreferencesKey("PENDING_FEED_LIKES_RESET_KEY")

            // 형식 변경이나 해석 코드 문제를 고친 뒤 되살릴 수 있도록, 해석하지 못한 원본을 모아 둡니다.
            private val PENDING_FEED_LIKES_UNREADABLE_KEY = stringSetPreferencesKey("PENDING_FEED_LIKES_UNREADABLE_KEY")

            /** 손상된 파일은 빈 기록으로 바꾸고, 초기화했다는 표시를 남깁니다. */
            internal val corruptionHandler = ReplaceFileCorruptionHandler {
                preferencesOf(PENDING_FEED_LIKES_RESET_KEY to true)
            }
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface PendingFeedLikeStoreModule {
    @Binds
    @Singleton
    fun bindPendingFeedLikeStore(defaultPendingFeedLikeStore: DefaultPendingFeedLikeStore): PendingFeedLikeStore
}
