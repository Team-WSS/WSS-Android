package com.into.websoso.data.feed.repository

import android.util.Log
import com.into.websoso.core.common.dispatchers.Dispatcher
import com.into.websoso.core.common.dispatchers.WebsosoDispatchers
import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.core.network.datasource.feed.model.request.CommentRequestDto
import com.into.websoso.data.feed.mapper.toData
import com.into.websoso.data.feed.model.CommentsEntity
import com.into.websoso.data.feed.model.FeedDetailEntity
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatedFeedRepository @Inject constructor(
    private val feedApi: FeedApi,
    @Dispatcher(WebsosoDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _sosoAllFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
    val sosoAllFeeds = _sosoAllFeeds.asStateFlow()

    private val _sosoRecommendedFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
    val sosoRecommendedFeeds = _sosoRecommendedFeeds.asStateFlow()

    private val _myFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
    val myFeeds = _myFeeds.asStateFlow()

    private val dirtyFeedStates = ConcurrentHashMap<Long, Boolean>()
    private val originalFeedStates = ConcurrentHashMap<Long, Boolean>()

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
        val result = feedApi.getFeeds(
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
     * 외부(UseCase 등)에서 가져온 내 피드 데이터를 캐시에 주입합니다.
     */
    fun updateMyFeedsCache(feeds: List<FeedEntity>, isRefreshed: Boolean) {
        val mergedFeeds = feeds.map { feed -> applyDirtyState(feed) }

        _myFeeds.update { current ->
            if (isRefreshed) mergedFeeds else (current + mergedFeeds).distinctBy { it.id }
        }
    }

    /**
     * 서버 데이터(Stale)보다 로컬의 변경사항(Fresh)을 우선 적용하여 반환합니다.
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
     * API 호출 없이 로컬 캐시의 좋아요 상태를 즉시 토글하고, 변경 내역을 Dirty Map에 기록합니다.
     */
    fun toggleLikeLocal(feedId: Long) {
        updateFeedInFlow(_sosoAllFeeds, feedId)
        updateFeedInFlow(_sosoRecommendedFeeds, feedId)
        updateFeedInFlow(_myFeeds, feedId)
    }

    private fun updateFeedInFlow(flow: MutableStateFlow<List<FeedEntity>>, feedId: Long) {
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
    private fun trackDirtyState(feedId: Long, original: Boolean, new: Boolean) {
        originalFeedStates.putIfAbsent(feedId, original)
        if (originalFeedStates[feedId] == new) {
            dirtyFeedStates.remove(feedId)
        } else {
            dirtyFeedStates[feedId] = new
        }
    }

    /**
     * Dirty Map에 기록된 변경된 좋아요 상태들을 백그라운드에서 서버와 동기화합니다.
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
     * 피드를 삭제하고, 성공 시 로컬 캐시(Flow)에서도 해당 피드를 제거합니다.
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
     * 피드를 스포일러로 신고하고, 성공 시 로컬 캐시(Flow)에 스포일러 상태를 반영합니다.
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
     * 피드를 부적절한 게시물로 신고하고, 성공 시 로컬 캐시(Flow)에서 제거합니다.
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

    private fun removeFromFlow(flow: MutableStateFlow<List<FeedEntity>>, feedId: Long) {
        flow.update { list -> list.filterNot { it.id == feedId } }
    }

    private fun markAsSpoilerInFlow(flow: MutableStateFlow<List<FeedEntity>>, feedId: Long) {
        flow.update { list ->
            list.map { if (it.id == feedId) it.copy(isSpoiler = true) else it }
        }
    }

    // ============================================================================================
    //  Feed Detail & Comments
    // ============================================================================================

    /**
     * 피드 상세 정보를 조회합니다.
     */
    suspend fun fetchFeed(feedId: Long): FeedDetailEntity {
        val rawDetail = feedApi.getFeed(feedId).toData()
        return applyDirtyStateToDetail(rawDetail)
    }

    /**
     * FeedDetailEntity용 로컬 상태 병합 로직입니다.
     */
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
     * 특정 피드의 댓글 목록을 조회합니다.
     */
    suspend fun fetchComments(feedId: Long): CommentsEntity {
        return feedApi.getComments(feedId).toData()
    }

    /**
     * 새 댓글을 등록합니다.
     */
    suspend fun saveComment(feedId: Long, comment: String) {
        val commentRequestDto = CommentRequestDto(commentContent = comment)
        feedApi.postComment(feedId, commentRequestDto)
    }

    /**
     * 기존 댓글을 수정합니다.
     */
    suspend fun saveModifiedComment(feedId: Long, commentId: Long, comment: String) {
        val commentRequestDto = CommentRequestDto(commentContent = comment)
        feedApi.putComment(feedId, commentId, commentRequestDto)
    }

    /**
     * 댓글을 삭제합니다.
     */
    suspend fun deleteComment(feedId: Long, commentId: Long) {
        feedApi.deleteComment(feedId, commentId)
    }

    /**
     * 댓글을 스포일러로 신고합니다.
     */
    suspend fun saveSpoilerComment(feedId: Long, commentId: Long) {
        feedApi.postSpoilerComment(feedId, commentId)
    }

    /**
     * 댓글을 부적절한 내용으로 신고합니다.
     */
    suspend fun saveImpertinenceComment(feedId: Long, commentId: Long) {
        feedApi.postImpertinenceComment(feedId, commentId)
    }
}
