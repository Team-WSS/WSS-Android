package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.FakeApi
import com.teamwss.websoso.data.mapper.FeedMapper.toData
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.remote.request.FeedsRequestDto

class DefaultFeedRepository(
    private val fakeApi: FakeApi,
) {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    suspend fun fetchFeeds(category: String?, lastFeedId: Long, size: Int): FeedsEntity {
        val requestBody = FeedsRequestDto(lastFeedId = lastFeedId, size = size)
        val result = when (category == null) {
            true -> fakeApi.getFeeds(feedsRequestDto = requestBody)
            false -> fakeApi.getFeedsByCategory(
                category = category,
                feedsRequestDto = requestBody,
            )
        }

        return result.toData(cachedFeeds)
            .also {
                _cachedFeeds.addAll(it.feeds)
            }
    }

    fun clearCachedFeeds() {
        _cachedFeeds.clear()
    }
}
