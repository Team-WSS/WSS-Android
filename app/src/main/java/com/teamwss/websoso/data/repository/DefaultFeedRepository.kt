package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.FakeApi
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import javax.inject.Inject

class DefaultFeedRepository @Inject constructor() {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    suspend fun fetchFeeds(category: String, lastFeedId: Long, size: Int): FeedsEntity {
        val requestBody = FeedsRequestDto(lastFeedId = lastFeedId, size = size)
        val result = FakeApi.getFeeds(
            category = if (category == "전체") null else category,
            feedsRequestDto = requestBody,
        )

        return result.toData()
            .also {
                _cachedFeeds.addAll(it.feeds)
            }
            .copy(feeds = cachedFeeds)
    }

    fun clearCachedFeeds() {
        if (cachedFeeds.isNotEmpty()) _cachedFeeds.clear()
    }
}
