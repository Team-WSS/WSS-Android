package com.into.websoso.data.feed.repository

import android.util.Log
import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.data.feed.mapper.toData
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("피드 QA 완료 후 제거 예정")
@Singleton
class FeedRepository
@Inject
constructor(
    private val feedApi: FeedApi,
) {
    // 내부 상태 캡슐화 (외부에서 직접 수정 불가)
    private val cachedFeeds = mutableListOf<FeedEntity>()
    private val cachedRecommendedFeeds = mutableListOf<FeedEntity>()

    /**
     * 커서 기반 피드 조회
     * @param lastFeedId: 현재 리스트의 마지막 ID (0일 경우 최신순 새로고침)
     * @param size: 가져올 개수
     * @param feedsOption: "RECOMMENDED" 또는 기타 옵션
     */
    suspend fun fetchFeeds(
        lastFeedId: Long,
        size: Int,
        feedsOption: String,
    ): FeedsEntity {
        // 1. 서버로부터 데이터 fetch
        val result = feedApi
            .getFeeds(
                feedsOption = feedsOption,
                category = null,
                lastFeedId = lastFeedId,
                size = size,
            ).toData()

        // 2. 타겟 캐시 결정
        val isRecommended = feedsOption == "RECOMMENDED"
        val targetCache = if (isRecommended) cachedRecommendedFeeds else cachedFeeds

        // 3. 커서(lastFeedId) 값에 따른 전략 수행
        // lastFeedId가 0이면 사용자가 새로고침을 했거나 처음 진입한 상황임
        if (lastFeedId == 0L) {
            targetCache.clear()
        }

        // 4. 데이터 중복 방지 및 추가 (Server-side Cursor의 신뢰성 확보)
        // 혹시라도 서버에서 중복 데이터가 내려올 경우를 대비해 ID 기준으로 필터링 후 추가
        val existingIds = targetCache.map { it.id }.toSet()
        val newUniqueFeeds = result.feeds.filterNot { it.id in existingIds }
        targetCache.addAll(newUniqueFeeds)

        // 5. 현재까지 누적된 전체 리스트를 담은 Entity 반환
        return result.copy(feeds = targetCache.toList())
    }

    /**
     * 특정 피드 삭제 및 로컬 동기화
     */
    suspend fun saveRemovedFeed(feedId: Long) {
        runCatching {
            feedApi.deleteFeed(feedId)
        }.onSuccess {
            // 모든 캐시에서 해당 피드 제거 (안전하게 두 곳 모두 확인 가능)
            cachedFeeds.removeIf { it.id == feedId }
            cachedRecommendedFeeds.removeIf { it.id == feedId }
        }.onFailure {
            Log.d("FeedRepository", "saveRemovedFeed 함수 failed : ${it.message}")
        }
    }

    suspend fun saveSpoilerFeed(feedId: Long) {
        feedApi.postSpoilerFeed(feedId)
    }

    suspend fun saveImpertinenceFeed(feedId: Long) {
        feedApi.postImpertinenceFeed(feedId)
    }

    suspend fun saveLike(
        isLikedOfLikedFeed: Boolean,
        selectedFeedId: Long,
    ) {
        when (isLikedOfLikedFeed) {
            true -> feedApi.deleteLikes(selectedFeedId)
            false -> feedApi.postLikes(selectedFeedId)
        }
    }
}
