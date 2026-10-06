package com.into.websoso.data.feed.repository

import android.net.Uri
import android.util.Log
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.common.image.ImageCompressor
import com.into.websoso.core.network.common.ImageDownloader
import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.mapper.MultiPartMapper
import com.into.websoso.core.network.datasource.feed.model.request.CommentRequestDto
import com.into.websoso.core.network.datasource.feed.model.request.FeedRequestDto
import com.into.websoso.data.feed.mapper.toData
import com.into.websoso.data.feed.model.CommentsEntity
import com.into.websoso.data.feed.model.FeedDetailEntity
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import com.into.websoso.data.feed.repository.model.CachedFeedLikeState
import com.into.websoso.data.feed.repository.model.LikeSyncStatus
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatedFeedRepository
    @Inject
    constructor(
        private val feedApi: FeedApi,
        private val pendingFeedLikeStore: PendingFeedLikeStore,
        private val multiPartMapper: MultiPartMapper,
        private val imageDownloader: ImageDownloader,
        private val imageCompressor: ImageCompressor,
        @Dispatcher(WebsosoDispatchers.IO) private val dispatcher: CoroutineDispatcher,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + dispatcher)

        private val _feedRefreshEvent = MutableSharedFlow<Unit>()
        val feedRefreshEvent = _feedRefreshEvent.asSharedFlow()

        private val _sosoAllFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
        val sosoAllFeeds = _sosoAllFeeds.asStateFlow()

        private val _sosoRecommendedFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
        val sosoRecommendedFeeds = _sosoRecommendedFeeds.asStateFlow()

        private val _myFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
        val myFeeds = _myFeeds.asStateFlow()

        private val pendingLikeStates = ConcurrentHashMap<Long, Boolean>()
        private val originalLikeStates = ConcurrentHashMap<Long, Boolean>()
        private val _feedDetailLikeStates = MutableStateFlow<Map<Long, CachedFeedLikeState>>(emptyMap())
        val feedDetailLikeStates = _feedDetailLikeStates.asStateFlow()
        private val pendingLikeStoreWriteMutex = Mutex()
        private val likeStateLock = Any()

        // ponytail: all feeds share one sender; use per-feed serialization if slow requests delay other feeds excessively.
        private val likeSyncMutex = Mutex()
        private val restoreMutex = Mutex()
        private var isLikesRestored = false
        private val _isLikeRestoreFailed = MutableStateFlow(false)
        val isLikeRestoreFailed = _isLikeRestoreFailed.asStateFlow()
        private val _hasUnrestoredLikes = MutableStateFlow(false)
        val hasUnrestoredLikes = _hasUnrestoredLikes.asStateFlow()
        private val unconfirmedLikeIds = mutableSetOf<Long>()
        private val likeRevisions = mutableMapOf<Long, Long>()

        // When a feed's like last changed by user input or server confirmation; fetched data older than this is ignored.
        private val likeChangedAt = mutableMapOf<Long, Long>()
        private val completedLikeAttempts = mutableMapOf<Long, Pair<Long, Long>>()
        private var likeEventSequence = 0L
        private val _likeSyncStates = MutableStateFlow<Map<Long, LikeSyncStatus>>(emptyMap())
        val likeSyncStates = _likeSyncStates.asStateFlow()

        init {
            scope.launch { ensureLikesRestored() }
        }

        /**
         * 저장된 좋아요를 아직 불러오지 못했다면 다시 불러옵니다.
         * 실패해도 예외를 던지지 않고 false를 반환해, 호출한 쪽이 저장·전송을 보류하게 합니다.
         */
        private suspend fun ensureLikesRestored(): Boolean {
            var recovered = false
            val restored = restoreMutex.withLock {
                if (isLikesRestored) return@withLock true
                try {
                    restorePendingLikes()
                    isLikesRestored = true
                    // An earlier attempt failed, so selections made meanwhile may be held only in memory.
                    recovered = _isLikeRestoreFailed.value
                    _isLikeRestoreFailed.value = false
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    _isLikeRestoreFailed.value = true
                    Log.e("UpdatedFeedRepository", "Failed to restore pending feed likes", error)
                }
                isLikesRestored
            }
            if (recovered) persistHeldLikes()
            return restored
        }

        /** 복원하지 못한 좋아요 안내를 보여 준 뒤 호출해, 같은 안내가 반복되지 않게 합니다. */
        fun acknowledgeUnrestoredLikes() {
            _hasUnrestoredLikes.value = false
            scope.launch {
                try {
                    pendingFeedLikeStore.clearResetNotice()
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    // If the notice stays, the user is told once more on the next launch.
                    Log.e("UpdatedFeedRepository", "Failed to clear the unrestored likes notice", error)
                }
            }
        }

        /** 복원에 실패한 동안 메모리에만 남은 선택을, 복원에 성공한 뒤 저장소에 저장합니다. */
        private suspend fun persistHeldLikes() {
            val feedIds = synchronized(likeStateLock) { pendingLikeStates.keys.toList() }
            feedIds.forEach { feedId -> persistPendingLikeOrMarkRetry(feedId) }
        }

        private suspend fun restorePendingLikes() {
            val pendingLikes = pendingFeedLikeStore.getPendingLikes()
            if (pendingFeedLikeStore.readResetNotice()) {
                // Saved changes were corrupted or unreadable and were reset, so they could not be restored.
                Log.e("UpdatedFeedRepository", "Saved pending feed likes could not be restored and were reset")
                _hasUnrestoredLikes.value = true
            }
            synchronized(likeStateLock) {
                pendingLikes.forEach { (id, isLiked) ->
                    // A click made while storage was loading takes precedence, including a cancelled selection.
                    pendingLikeStates[id] = if (id in likeRevisions) {
                        findCurrentLikeState(id) ?: pendingLikeStates[id] ?: isLiked
                    } else {
                        isLiked
                    }
                    unconfirmedLikeIds += id
                    setLikeSyncStatus(id, LikeSyncStatus.NEEDS_RETRY)
                }
                applyPendingLikeStatesToCachedFeeds()
            }
        }

        // ============================================================================================
        //  Feed Creation & Modification
        // ============================================================================================

        /**
         * 피드를 서버에 생성합니다.
         * 완료 후 전체 리스트를 새로고침하도록 이벤트를 발생시킵니다.
         */
        suspend fun saveFeed(
            feedContent: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
            images: List<Uri>,
        ) {
            feedApi.postFeed(
                feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                    target = FeedRequestDto(
                        feedContent = feedContent,
                        novelId = novelId,
                        isSpoiler = isSpoiler,
                        isPublic = isPublic,
                    ),
                    partName = PART_NAME_FEED,
                    fileName = FILE_NAME_FEED_JSON,
                ),
                images = images.map { multiPartMapper.formatToMultipart(it) },
            )

            _feedRefreshEvent.emit(Unit)
        }

        /**
         * 기존 피드를 수정합니다.
         * 로컬 캐시의 데이터를 즉시 교체하여 화면에 반영한 뒤, 백그라운드에서 서버와 동기화합니다.
         */
        fun saveEditedFeed(
            feedId: Long,
            editedFeed: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
            images: List<Uri>,
        ) {
            updateFeedInLocalCache(feedId, editedFeed, isSpoiler, isPublic)

            scope.launch {
                runCatching {
                    feedApi.putFeed(
                        feedId = feedId,
                        feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                            target = FeedRequestDto(
                                feedContent = editedFeed,
                                novelId = novelId,
                                isSpoiler = isSpoiler,
                                isPublic = isPublic,
                            ),
                            partName = PART_NAME_FEED,
                            fileName = FILE_NAME_FEED_JSON,
                        ),
                        images = images.map { multiPartMapper.formatToMultipart(it) },
                    )
                }.onFailure {
                    Log.e("UpdatedFeedRepository", "Failed to sync edited feed", it)
                }
            }
        }

        /**
         * 로컬 Flow에 저장된 리스트 중 특정 피드의 내용만 갱신합니다.
         */
        private fun updateFeedInLocalCache(
            feedId: Long,
            editedFeed: String,
            isSpoiler: Boolean,
            isPublic: Boolean,
        ) {
            val updateAction: (List<FeedEntity>) -> List<FeedEntity> = { list ->
                list.map { feed ->
                    if (feed.id == feedId) {
                        feed.copy(
                            content = editedFeed,
                            isSpoiler = isSpoiler,
                            isPublic = isPublic,
                        )
                    } else {
                        feed
                    }
                }
            }

            _sosoAllFeeds.update(updateAction)
            _sosoRecommendedFeeds.update(updateAction)
            _myFeeds.update(updateAction)
        }

        /**
         * 이미지 URL을 Uri 객체로 다운로드하여 반환합니다.
         */
        suspend fun downloadImage(imageUrl: String): Result<Uri?> = imageDownloader.formatImageToUri(imageUrl)

        /**
         * 선택된 이미지 Uri 리스트를 압축하여 반환합니다.
         */
        suspend fun compressImages(imageUris: List<Uri>): List<Uri> = imageCompressor.compressUris(imageUris)

        // ============================================================================================
        //  Feed List & Caching Logic
        // ============================================================================================

        /**
         * 서버에서 피드 리스트를 조회하고, 로컬의 미동기화된 좋아요 상태를 병합하여 캐시(Flow)를 갱신합니다.
         */
        suspend fun fetchFeeds(
            lastFeedId: Long,
            size: Int,
            feedsOption: String,
        ): FeedsEntity {
            val version = likeStateVersion()
            val result = feedApi
                .getFeeds(
                    feedsOption = feedsOption,
                    lastFeedId = lastFeedId,
                    size = size,
                ).toData()

            return synchronized(likeStateLock) {
                val mergedFeeds = result.feeds.map { feed -> applyLatestLikeState(feed, version) }
                updateCachedLikes(mergedFeeds.associate { it.id to CachedFeedLikeState(it.isLiked, it.likeCount) })

                val isRecommended = feedsOption == "RECOMMENDED"
                val targetFlow = if (isRecommended) _sosoRecommendedFeeds else _sosoAllFeeds

                targetFlow.update { currentList ->
                    if (lastFeedId == 0L) {
                        mergedFeeds
                    } else {
                        val newFeeds = mergedFeeds.filterNot { new -> currentList.any { it.id == new.id } }
                        currentList + newFeeds
                    }
                }

                result.copy(feeds = targetFlow.value)
            }
        }

        /**
         * 외부에서 가져온 내 피드 데이터를 캐시에 주입합니다.
         * likeStateVersion에는 조회를 시작하기 직전에 받아 둔 좋아요 상태 번호를 넘깁니다.
         */
        fun updateMyFeedsCache(
            feeds: List<FeedEntity>,
            isRefreshed: Boolean,
            likeStateVersion: Long,
        ) {
            synchronized(likeStateLock) {
                val mergedFeeds = feeds.map { feed -> applyLatestLikeState(feed, likeStateVersion) }
                updateCachedLikes(mergedFeeds.associate { it.id to CachedFeedLikeState(it.isLiked, it.likeCount) })

                _myFeeds.update { current ->
                    if (isRefreshed) mergedFeeds else (current + mergedFeeds).distinctBy { it.id }
                }
            }
        }

        /**
         * 서버 데이터보다 로컬의 변경사항(좋아요)을 우선 적용하여 반환합니다.
         */
        private fun applyPendingLikeState(feed: FeedEntity): FeedEntity =
            synchronized(likeStateLock) {
                val localIsLiked = pendingLikeStates[feed.id] ?: return@synchronized feed
                originalLikeStates.putIfAbsent(feed.id, feed.isLiked)
                if (feed.isLiked == localIsLiked) return@synchronized feed
                feed.copy(
                    isLiked = localIsLiked,
                    likeCount = (feed.likeCount + if (localIsLiked) 1 else -1).coerceAtLeast(0),
                )
            }

        private fun updateCachedLikes(likes: Map<Long, CachedFeedLikeState>) {
            listOf(_sosoAllFeeds, _sosoRecommendedFeeds, _myFeeds).forEach { flow ->
                flow.update { feeds ->
                    feeds.map { feed ->
                        val state = likes[feed.id]
                        if (state == null) feed else feed.copy(isLiked = state.isLiked, likeCount = state.likeCount)
                    }
                }
            }
            _feedDetailLikeStates.update { states ->
                states.mapValues { (id, state) -> likes[id] ?: state }
            }
        }

        private fun applyPendingLikeStatesToCachedFeeds() {
            val updateAction: (List<FeedEntity>) -> List<FeedEntity> = { list ->
                list.map { feed -> applyPendingLikeState(feed) }
            }

            _sosoAllFeeds.update(updateAction)
            _sosoRecommendedFeeds.update(updateAction)
            _myFeeds.update(updateAction)
            _feedDetailLikeStates.update { states ->
                states.mapValues { (id, state) ->
                    val desired = pendingLikeStates[id]
                    if (desired == null || desired == state.isLiked) {
                        state
                    } else {
                        state.copy(isLiked = desired, likeCount = (state.likeCount + if (desired) 1 else -1).coerceAtLeast(0))
                    }
                }
            }
        }

        // ============================================================================================
        //  Interaction (Like, Sync)
        // ============================================================================================

        /**
         * 로컬 캐시의 좋아요 상태를 즉시 토글하고 변경 내역을 기록합니다.
         */
        fun toggleLikeLocal(feedId: Long) {
            synchronized(likeStateLock) {
                val current = findCurrentLikeState(feedId) ?: return
                val newLiked = !current
                // Record the user's input once, even when the same feed is cached in several lists.
                val sequence = ++likeEventSequence
                likeRevisions[feedId] = sequence
                likeChangedAt[feedId] = sequence
                trackPendingLikeState(feedId, current, newLiked)
                val update: (List<FeedEntity>) -> List<FeedEntity> = { feeds ->
                    feeds.map { feed ->
                        if (feed.id != feedId || feed.isLiked == newLiked) {
                            feed
                        } else {
                            feed.copy(
                                isLiked = newLiked,
                                likeCount = (feed.likeCount + if (newLiked) 1 else -1).coerceAtLeast(0),
                            )
                        }
                    }
                }
                _sosoAllFeeds.update(update)
                _sosoRecommendedFeeds.update(update)
                _myFeeds.update(update)
                _feedDetailLikeStates.value[feedId]?.let { detail ->
                    _feedDetailLikeStates.update { states ->
                        states + (
                            feedId to detail.copy(
                                isLiked = newLiked,
                                likeCount = (
                                    detail.likeCount + if (detail.isLiked == newLiked) {
                                        0
                                    } else if (newLiked) {
                                        1
                                    } else {
                                        -1
                                    }
                                ).coerceAtLeast(0),
                            )
                        )
                    }
                }
            }
        }

        private fun findCachedFeed(feedId: Long): FeedEntity? =
            _sosoAllFeeds.value.find { it.id == feedId }
                ?: _sosoRecommendedFeeds.value.find { it.id == feedId }
                ?: _myFeeds.value.find { it.id == feedId }

        /**
         * 서버에 아직 반영되지 않은 마지막 좋아요 상태를 추적합니다.
         */
        private fun trackPendingLikeState(
            feedId: Long,
            original: Boolean,
            new: Boolean,
        ) {
            originalLikeStates.putIfAbsent(feedId, original)
            if (feedId !in unconfirmedLikeIds && originalLikeStates[feedId] == new) {
                pendingLikeStates.remove(feedId)
                originalLikeStates.remove(feedId)
                setLikeSyncStatus(feedId, null)
            } else {
                pendingLikeStates[feedId] = new
            }
            scope.launch {
                // Restore failed: keep the selection in memory and hold the write.
                if (!ensureLikesRestored()) return@launch
                persistPendingLikeOrMarkRetry(feedId)
            }
        }

        private suspend fun persistPendingLikeOrMarkRetry(feedId: Long) {
            try {
                persistPendingLike(feedId)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                synchronized(likeStateLock) {
                    if (pendingLikeStates.containsKey(feedId)) setLikeSyncStatus(feedId, LikeSyncStatus.NEEDS_RETRY)
                }
                Log.e("UpdatedFeedRepository", "Failed to save pending feed like $feedId", error)
            }
        }

        // Read the latest selection when the write runs; a delayed write must not resurrect an old selection.
        private suspend fun persistPendingLike(feedId: Long) {
            pendingLikeStoreWriteMutex.withLock {
                val latest = synchronized(likeStateLock) { pendingLikeStates[feedId] }
                if (latest == null) {
                    pendingFeedLikeStore.deletePendingLike(feedId)
                } else {
                    pendingFeedLikeStore.updatePendingLike(feedId, latest)
                }
            }
        }

        /** Screen disposal sends all remaining selections, including earlier failures. */
        fun syncPendingLikes() = syncPendingLikes(null)

        /** A manual retry targets only unresolved items, using their latest selection. */
        fun retryPendingLikes(feedIds: Set<Long>? = null) {
            if (_isLikeRestoreFailed.value) {
                // Restore failed earlier: restore first, then retry including the restored items.
                scope.launch { if (ensureLikesRestored()) retryPendingLikes(feedIds) }
                return
            }
            val targets = synchronized(likeStateLock) {
                _likeSyncStates.value
                    .filter { (id, status) -> status == LikeSyncStatus.NEEDS_RETRY && (feedIds == null || id in feedIds) }
                    .keys
                    .toSet()
            }
            if (targets.isNotEmpty()) syncPendingLikes(targets)
        }

        private fun syncPendingLikes(feedIds: Set<Long>?) {
            val requestedAt = synchronized(likeStateLock) { ++likeEventSequence }
            scope.launch {
                // Restore failed: hold sending until a later sync or retry restores the saved likes.
                if (!ensureLikesRestored()) return@launch
                likeSyncMutex.withLock {
                    val targets = synchronized(likeStateLock) {
                        pendingLikeStates.keys.filter { feedIds == null || it in feedIds }
                    }
                    targets.forEach { id -> syncPendingLike(id, requestedAt) }
                }
            }
        }

        private suspend fun syncPendingLike(
            feedId: Long,
            requestedAt: Long,
        ) {
            val selection = synchronized(likeStateLock) {
                val desired = pendingLikeStates[feedId] ?: return
                val revision = likeRevisions[feedId] ?: 0L
                val completed = completedLikeAttempts[feedId]
                // Coalesce triggers that overlapped the same attempt, including a failed attempt.
                if (completed != null && completed.first == revision && completed.second > requestedAt) return
                val needsNetwork = feedId in unconfirmedLikeIds || originalLikeStates[feedId] != desired
                if (needsNetwork) unconfirmedLikeIds += feedId
                setLikeSyncStatus(feedId, LikeSyncStatus.SYNCING)
                LikeSyncRequest(desired, revision, needsNetwork)
            }
            var serverConfirmed = !selection.needsNetwork
            try {
                persistPendingLike(feedId)
                if (selection.needsNetwork) {
                    if (selection.isLiked) feedApi.postLikes(feedId) else feedApi.deleteLikes(feedId)
                    serverConfirmed = true
                }
                synchronized(likeStateLock) {
                    originalLikeStates[feedId] = selection.isLiked
                    unconfirmedLikeIds.remove(feedId)
                    if (pendingLikeStates[feedId] == selection.isLiked) {
                        pendingLikeStates.remove(feedId)
                    }
                    // Mark the server change in the same lock as the pending cleanup so an older fetch cannot slip in.
                    likeChangedAt[feedId] = ++likeEventSequence
                    // An older success does not confirm an input made while the request was in flight.
                    setLikeSyncStatus(feedId, if (pendingLikeStates.containsKey(feedId)) LikeSyncStatus.NEEDS_RETRY else null)
                }
                persistPendingLike(feedId)
                synchronized(likeStateLock) {
                    if (!pendingLikeStates.containsKey(feedId)) originalLikeStates.remove(feedId)
                }
            } catch (error: Exception) {
                synchronized(likeStateLock) {
                    // Even cancellation/timeout says nothing about whether the server applied the request.
                    if (!serverConfirmed) unconfirmedLikeIds += feedId
                    pendingLikeStates.putIfAbsent(feedId, findCurrentLikeState(feedId) ?: selection.isLiked)
                    setLikeSyncStatus(feedId, LikeSyncStatus.NEEDS_RETRY)
                }
                if (error is CancellationException) throw error
                Log.e("UpdatedFeedRepository", "Failed to sync feed $feedId", error)
            } finally {
                synchronized(likeStateLock) {
                    completedLikeAttempts[feedId] = selection.revision to ++likeEventSequence
                }
            }
        }

        private fun setLikeSyncStatus(
            feedId: Long,
            status: LikeSyncStatus?,
        ) {
            _likeSyncStates.update { states ->
                if (status == null) states - feedId else states + (feedId to status)
            }
        }

        private data class LikeSyncRequest(
            val isLiked: Boolean,
            val revision: Long,
            val needsNetwork: Boolean,
        )

        private fun findCurrentLikeState(feedId: Long): Boolean? =
            findCachedFeed(feedId)?.isLiked ?: _feedDetailLikeStates.value[feedId]?.isLiked

        /** 조회를 시작하기 직전에 받아 두는 번호입니다. 응답을 반영할 때 그 뒤에 좋아요가 바뀌었는지 비교합니다. */
        fun likeStateVersion(): Long = synchronized(likeStateLock) { likeEventSequence }

        private fun hasLikeChangedSince(
            feedId: Long,
            version: Long,
        ): Boolean = (likeChangedAt[feedId] ?: 0L) > version

        private fun findCurrentLike(feedId: Long): CachedFeedLikeState? =
            findCachedFeed(feedId)?.let { CachedFeedLikeState(it.isLiked, it.likeCount) } ?: _feedDetailLikeStates.value[feedId]

        private fun applyLatestLikeState(
            feed: FeedEntity,
            version: Long,
        ): FeedEntity {
            if (hasLikeChangedSince(feed.id, version)) {
                // The like changed while this response was in flight, so keep what the user sees now.
                findCurrentLike(feed.id)?.let { return feed.copy(isLiked = it.isLiked, likeCount = it.likeCount) }
            }
            return applyPendingLikeState(feed)
        }

        private fun applyLatestLikeStateToDetail(
            feed: FeedDetailEntity,
            version: Long,
        ): FeedDetailEntity {
            if (hasLikeChangedSince(feed.id, version)) {
                // The like changed while this response was in flight, so keep what the user sees now.
                findCurrentLike(feed.id)?.let { return feed.copy(isLiked = it.isLiked, likeCount = it.likeCount) }
            }
            return applyPendingLikeStateToDetail(feed)
        }

        // ============================================================================================
        //  Feed Actions (Remove, Report)
        // ============================================================================================

        /**
         * 피드를 삭제합니다.
         */
        suspend fun saveRemovedFeed(feedId: Long) {
            runCatching {
                feedApi.deleteFeed(feedId)
            }.onSuccess {
                removeFromFlow(_sosoAllFeeds, feedId)
                removeFromFlow(_sosoRecommendedFeeds, feedId)
                removeFromFlow(_myFeeds, feedId)
            }
        }

        /**
         * 피드를 스포일러로 신고합니다.
         */
        suspend fun saveSpoilerFeed(feedId: Long) {
            runCatching {
                feedApi.postSpoilerFeed(feedId)
            }.onSuccess {
                markAsSpoilerInFlow(_sosoAllFeeds, feedId)
                markAsSpoilerInFlow(_sosoRecommendedFeeds, feedId)
                markAsSpoilerInFlow(_myFeeds, feedId)
            }
        }

        /**
         * 피드를 부적절한 게시물로 신고합니다.
         */
        suspend fun saveImpertinenceFeed(feedId: Long) {
            runCatching {
                feedApi.postImpertinenceFeed(feedId)
            }.onSuccess {
                removeFromFlow(_sosoAllFeeds, feedId)
                removeFromFlow(_sosoRecommendedFeeds, feedId)
                removeFromFlow(_myFeeds, feedId)
            }
        }

        private fun removeFromFlow(
            flow: MutableStateFlow<List<FeedEntity>>,
            feedId: Long,
        ) {
            flow.update { list -> list.filterNot { it.id == feedId } }
        }

        private fun markAsSpoilerInFlow(
            flow: MutableStateFlow<List<FeedEntity>>,
            feedId: Long,
        ) {
            flow.update { list ->
                list.map { if (it.id == feedId) it.copy(isSpoiler = true) else it }
            }
        }

        // ============================================================================================
        //  Feed Detail & Comments
        // ============================================================================================

        /**
         * 피드 상세 정보를 조회하고 로컬 상태를 병합하여 반환합니다.
         */
        suspend fun fetchFeed(feedId: Long): FeedDetailEntity {
            val version = likeStateVersion()
            val rawDetail = feedApi.getFeed(feedId).toData()
            return synchronized(likeStateLock) {
                val mergedDetail = applyLatestLikeStateToDetail(rawDetail, version)
                val likeState = CachedFeedLikeState(mergedDetail.isLiked, mergedDetail.likeCount)
                _feedDetailLikeStates.update { it + (feedId to likeState) }
                updateCachedLikes(mapOf(feedId to likeState))
                mergedDetail
            }
        }

        private fun applyPendingLikeStateToDetail(feed: FeedDetailEntity): FeedDetailEntity =
            synchronized(likeStateLock) {
                val localIsLiked = pendingLikeStates[feed.id] ?: return@synchronized feed
                originalLikeStates.putIfAbsent(feed.id, feed.isLiked)

                if (feed.isLiked != localIsLiked) {
                    val adjustedCount = if (localIsLiked) feed.likeCount + 1 else feed.likeCount - 1
                    return@synchronized feed.copy(
                        isLiked = localIsLiked,
                        likeCount = adjustedCount.coerceAtLeast(0),
                    )
                }
                return@synchronized feed
            }

        /**
         * 댓글 목록을 조회합니다.
         */
        suspend fun fetchComments(feedId: Long): CommentsEntity = feedApi.getComments(feedId).toData()

        /**
         * 댓글을 등록합니다.
         */
        suspend fun saveComment(
            feedId: Long,
            comment: String,
        ) {
            val commentRequestDto = CommentRequestDto(commentContent = comment)
            feedApi.postComment(feedId, commentRequestDto)
        }

        /**
         * 기존 댓글을 수정합니다.
         */
        suspend fun saveModifiedComment(
            feedId: Long,
            commentId: Long,
            comment: String,
        ) {
            val commentRequestDto = CommentRequestDto(commentContent = comment)
            feedApi.putComment(feedId, commentId, commentRequestDto)
        }

        /**
         * 댓글을 삭제합니다.
         */
        suspend fun deleteComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.deleteComment(feedId, commentId)
        }

        /**
         * 댓글을 스포일러로 신고합니다.
         */
        suspend fun saveSpoilerComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.postSpoilerComment(feedId, commentId)
        }

        /**
         * 댓글을 부적절한 내용으로 신고합니다.
         */
        suspend fun saveImpertinenceComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.postImpertinenceComment(feedId, commentId)
        }

        companion object {
            private const val PART_NAME_FEED: String = "feed"
            private const val FILE_NAME_FEED_JSON: String = "feed.json"
        }
    }
