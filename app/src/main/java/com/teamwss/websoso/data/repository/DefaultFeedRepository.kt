package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.FeedMapper.toData
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.request.FeedsRequestDto

class DefaultFeedRepository(
    private val feedApi: FeedApi,
) {

    suspend fun getFeeds(category: String, lastFeedId: Long, size: Int): List<FeedEntity> {
        val feeds = feedApi.getFeed(
            category = category, feedsRequestDto = FeedsRequestDto(
                lastFeedId = lastFeedId,
                size = size,
            )
        )

        return if (feeds.category == category) feeds.feedsResponseDto.map { it.toData() }
        else throw IllegalArgumentException()
    }
}
