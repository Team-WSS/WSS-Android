package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.data.feed.mapper.toData
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

/**
 * 비교군: 7de9b6497 시점 UpdatedFeedRepository의 좋아요 동기화 부분만 옮긴 테스트 전용 복사본.
 * 좋아요와 무관한 메서드와 로그만 뺐고, 동작은 바꾸지 않았다.
 * 저장소가 없어 앱이 종료되면 보내지 못한 선택이 사라지고, 실패한 요청은 다시 보내지 않는다.
 */
internal class LegacyFeedLikeRepository(
    private val feedApi: FeedApi,
    dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private val _sosoAllFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())
    val sosoAllFeeds = _sosoAllFeeds.asStateFlow()

    private val sosoRecommendedFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())

    private val myFeeds = MutableStateFlow<List<FeedEntity>>(emptyList())

    private val dirtyFeedStates = ConcurrentHashMap<Long, Boolean>()
    private val originalFeedStates = ConcurrentHashMap<Long, Boolean>()

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

        val mergedFeeds = result.feeds.map { feed -> applyDirtyState(feed) }

        val isRecommended = feedsOption == "RECOMMENDED"
        val targetFlow = if (isRecommended) sosoRecommendedFeeds else _sosoAllFeeds

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

    fun toggleLikeLocal(feedId: Long) {
        updateFeedInFlow(_sosoAllFeeds, feedId)
        updateFeedInFlow(sosoRecommendedFeeds, feedId)
        updateFeedInFlow(myFeeds, feedId)
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

    fun syncDirtyFeeds() {
        if (dirtyFeedStates.isEmpty()) return

        val syncMap = dirtyFeedStates.toMap()
        dirtyFeedStates.clear()
        originalFeedStates.clear()

        scope.launch {
            syncMap.forEach { (id, isLiked) ->
                runCatching {
                    if (isLiked) feedApi.postLikes(id) else feedApi.deleteLikes(id)
                }
            }
        }
    }
}
