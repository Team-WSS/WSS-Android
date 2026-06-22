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
import com.into.websoso.data.feed.store.PendingFeedLikeStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        private val feedDetailStates = ConcurrentHashMap<Long, CachedFeedLikeState>()
        private val pendingLikeStoreWriteLock = Any()
        private var pendingLikeStoreWriteJob: Job? = null

        init {
            restorePendingLikes()
        }

        private fun restorePendingLikes() {
            scope.launch {
                val pendingLikes = pendingFeedLikeStore.getPendingLikes()
                pendingLikeStates.clear()
                pendingLikeStates.putAll(pendingLikes)
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
            val result = feedApi
                .getFeeds(
                    feedsOption = feedsOption,
                    lastFeedId = lastFeedId,
                    size = size,
                ).toData()

            val mergedFeeds = result.feeds.map { feed -> applyPendingLikeState(feed) }

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

            return result.copy(feeds = targetFlow.value)
        }

        /**
         * 외부에서 가져온 내 피드 데이터를 캐시에 주입합니다.
         */
        fun updateMyFeedsCache(
            feeds: List<FeedEntity>,
            isRefreshed: Boolean,
        ) {
            val mergedFeeds = feeds.map { feed -> applyPendingLikeState(feed) }

            _myFeeds.update { current ->
                if (isRefreshed) mergedFeeds else (current + mergedFeeds).distinctBy { it.id }
            }
        }

        /**
         * 서버 데이터보다 로컬의 변경사항(좋아요)을 우선 적용하여 반환합니다.
         */
        private fun applyPendingLikeState(feed: FeedEntity): FeedEntity {
            val localIsLiked = pendingLikeStates[feed.id] ?: return feed

            if (feed.isLiked != localIsLiked) {
                val adjustedCount = if (localIsLiked) feed.likeCount + 1 else feed.likeCount - 1
                return feed.copy(
                    isLiked = localIsLiked,
                    likeCount = adjustedCount.coerceAtLeast(0),
                )
            }
            return feed
        }

        private fun applyPendingLikeStatesToCachedFeeds() {
            val updateAction: (List<FeedEntity>) -> List<FeedEntity> = { list ->
                list.map { feed -> applyPendingLikeState(feed) }
            }

            _sosoAllFeeds.update(updateAction)
            _sosoRecommendedFeeds.update(updateAction)
            _myFeeds.update(updateAction)
        }

        // ============================================================================================
        //  Interaction (Like, Sync)
        // ============================================================================================

        /**
         * 로컬 캐시의 좋아요 상태를 즉시 토글하고 변경 내역을 기록합니다.
         */
        fun toggleLikeLocal(feedId: Long) {
            updateFeedInFlow(_sosoAllFeeds, feedId)
            updateFeedInFlow(_sosoRecommendedFeeds, feedId)
            updateFeedInFlow(_myFeeds, feedId)

            if (findCachedFeed(feedId) == null) {
                updateFeedDetailState(feedId)
            }
        }

        private fun updateFeedInFlow(
            flow: MutableStateFlow<List<FeedEntity>>,
            feedId: Long,
        ) {
            flow.update { list ->
                val index = list.indexOfFirst { it.id == feedId }
                if (index == -1) return@update list

                val target = list[index]
                val newLiked = !target.isLiked
                val newCount = if (newLiked) target.likeCount + 1 else target.likeCount - 1

                trackPendingLikeState(feedId, target.isLiked, newLiked)

                val newList = list.toMutableList()
                newList[index] = target.copy(isLiked = newLiked, likeCount = newCount)
                newList
            }
        }

        private fun updateFeedDetailState(feedId: Long) {
            val target = feedDetailStates[feedId] ?: return
            val newLiked = !target.isLiked
            val newCount = if (newLiked) target.likeCount + 1 else target.likeCount - 1

            trackPendingLikeState(feedId, target.isLiked, newLiked)
            feedDetailStates[feedId] = CachedFeedLikeState(
                isLiked = newLiked,
                likeCount = newCount.coerceAtLeast(0),
            )
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
            if (originalLikeStates[feedId] == new) {
                pendingLikeStates.remove(feedId)
                deletePendingLike(feedId)
            } else {
                pendingLikeStates[feedId] = new
                updatePendingLike(feedId, new)
            }
        }

        private fun updatePendingLike(
            feedId: Long,
            isLiked: Boolean,
        ) {
            enqueuePendingLikeStoreWrite(feedId, "save") {
                pendingFeedLikeStore.updatePendingLike(feedId, isLiked)
            }
        }

        private fun deletePendingLike(feedId: Long) {
            enqueuePendingLikeStoreWrite(feedId, "delete") {
                pendingFeedLikeStore.deletePendingLike(feedId)
            }
        }

        private fun enqueuePendingLikeStoreWrite(
            feedId: Long,
            actionName: String,
            action: suspend () -> Unit,
        ) {
            synchronized(pendingLikeStoreWriteLock) {
                val previousJob = pendingLikeStoreWriteJob
                pendingLikeStoreWriteJob = scope.launch {
                    previousJob?.join()
                    runCatching {
                        action()
                    }.onFailure {
                        Log.e(
                            "UpdatedFeedRepository",
                            "Failed to $actionName pending feed like $feedId",
                            it,
                        )
                    }
                }
            }
        }

        /**
         * 서버에 아직 반영되지 않은 좋아요 상태들을 동기화합니다.
         */
        fun syncPendingLikes() {
            scope.launch {
                val syncMap = pendingFeedLikeStore.getPendingLikes() + pendingLikeStates.toMap()
                if (syncMap.isEmpty()) return@launch

                syncMap.forEach { (id, isLiked) ->
                    runCatching {
                        if (isLiked) feedApi.postLikes(id) else feedApi.deleteLikes(id)
                    }.onSuccess {
                        deleteSyncedPendingLikeFromStore(id, isLiked)
                        deleteSyncedPendingLikeFromMemory(id, isLiked)
                    }.onFailure {
                        Log.e("UpdatedFeedRepository", "Failed to sync feed $id", it)
                    }
                }
            }
        }

        private fun deleteSyncedPendingLikeFromMemory(
            feedId: Long,
            isLiked: Boolean,
        ) {
            pendingLikeStates.remove(feedId, isLiked)
            if (!pendingLikeStates.containsKey(feedId)) {
                originalLikeStates.remove(feedId)
            }
        }

        private suspend fun deleteSyncedPendingLikeFromStore(
            feedId: Long,
            isLiked: Boolean,
        ) {
            runCatching {
                pendingFeedLikeStore.deletePendingLikeIfMatched(feedId, isLiked)
            }.onFailure {
                Log.e("UpdatedFeedRepository", "Failed to delete synced feed like $feedId", it)
            }
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
            val rawDetail = feedApi.getFeed(feedId).toData()
            val mergedDetail = applyPendingLikeStateToDetail(rawDetail)
            feedDetailStates[feedId] = CachedFeedLikeState(
                isLiked = mergedDetail.isLiked,
                likeCount = mergedDetail.likeCount,
            )
            return mergedDetail
        }

        private fun applyPendingLikeStateToDetail(feed: FeedDetailEntity): FeedDetailEntity {
            val localIsLiked = pendingLikeStates[feed.id] ?: return feed

            if (feed.isLiked != localIsLiked) {
                val adjustedCount = if (localIsLiked) feed.likeCount + 1 else feed.likeCount - 1
                return feed.copy(
                    isLiked = localIsLiked,
                    likeCount = adjustedCount.coerceAtLeast(0),
                )
            }
            return feed
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
