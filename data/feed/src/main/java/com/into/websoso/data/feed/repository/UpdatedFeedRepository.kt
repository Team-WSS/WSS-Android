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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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

        private val dirtyFeedStates = ConcurrentHashMap<Long, Boolean>()
        private val originalFeedStates = ConcurrentHashMap<Long, Boolean>()

        // ============================================================================================
        //  Feed Creation & Modification
        // ============================================================================================

        /**
         * 피드를 서버에 생성합니다.
         * 완료 후 전체 리스트를 새로고침하도록 이벤트를 발생시킵니다.
         */
        suspend fun saveFeed(
            relevantCategories: List<String>,
            feedContent: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
            images: List<Uri>,
        ) {
            feedApi.postFeed(
                feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                    target = FeedRequestDto(
                        relevantCategories = relevantCategories,
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
            relevantCategories: List<String>,
            editedFeed: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
            images: List<Uri>,
        ) {
            updateFeedInLocalCache(feedId, editedFeed, relevantCategories, isSpoiler, isPublic)

            scope.launch {
                runCatching {
                    feedApi.putFeed(
                        feedId = feedId,
                        feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                            target = FeedRequestDto(
                                relevantCategories = relevantCategories,
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
            relevantCategories: List<String>,
            isSpoiler: Boolean,
            isPublic: Boolean,
        ) {
            val updateAction: (List<FeedEntity>) -> List<FeedEntity> = { list ->
                list.map { feed ->
                    if (feed.id == feedId) {
                        feed.copy(
                            content = editedFeed,
                            relevantCategories = relevantCategories,
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
         * 서버에서 피드 리스트를 조회하고, 로컬의 미동기화된 좋아요 상태(Dirty)를 병합하여 캐시(Flow)를 갱신합니다.
         */
        suspend fun fetchFeeds(
            lastFeedId: Long,
            size: Int,
            feedsOption: String,
        ): FeedsEntity {
            val result = feedApi
                .getFeeds(
                    feedsOption = feedsOption,
                    category = null,
                    lastFeedId = lastFeedId,
                    size = size,
                ).toData()

            val mergedFeeds = result.feeds.map { feed -> applyDirtyState(feed) }

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
            val mergedFeeds = feeds.map { feed -> applyDirtyState(feed) }

            _myFeeds.update { current ->
                if (isRefreshed) mergedFeeds else (current + mergedFeeds).distinctBy { it.id }
            }
        }

        /**
         * 서버 데이터보다 로컬의 변경사항(좋아요)을 우선 적용하여 반환합니다.
         */
        private fun applyDirtyState(feed: FeedEntity): FeedEntity {
            val localIsLiked = dirtyFeedStates[feed.id] ?: return feed

            if (feed.isLiked != localIsLiked) {
                val adjustedCount = if (localIsLiked) feed.likeCount + 1 else feed.likeCount - 1
                return feed.copy(
                    isLiked = localIsLiked,
                    likeCount = adjustedCount.coerceAtLeast(0),
                )
            }
            return feed
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

                trackDirtyState(feedId, target.isLiked, newLiked)

                val newList = list.toMutableList()
                newList[index] = target.copy(isLiked = newLiked, likeCount = newCount)
                newList
            }
        }

        /**
         * 변경된 좋아요 상태를 추적합니다.
         */
        private fun trackDirtyState(
            feedId: Long,
            original: Boolean,
            new: Boolean,
        ) {
            originalFeedStates.putIfAbsent(feedId, original)
            if (originalFeedStates[feedId] == new) {
                dirtyFeedStates.remove(feedId)
            } else {
                dirtyFeedStates[feedId] = new
            }
        }

        /**
         * 기록된 변경 사항들을 서버와 동기화합니다.
         */
        fun syncDirtyFeeds() {
            if (dirtyFeedStates.isEmpty()) return

            val syncMap = dirtyFeedStates.toMap()
            dirtyFeedStates.clear()
            originalFeedStates.clear()

            scope.launch {
                syncMap.forEach { (id, isLiked) ->
                    runCatching {
                        if (isLiked) feedApi.postLikes(id) else feedApi.deleteLikes(id)
                    }.onFailure {
                        Log.e("UpdatedFeedRepository", "Failed to sync feed $id", it)
                    }
                }
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
            return applyDirtyStateToDetail(rawDetail)
        }

        private fun applyDirtyStateToDetail(feed: FeedDetailEntity): FeedDetailEntity {
            val localIsLiked = dirtyFeedStates[feed.id] ?: return feed

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
