package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.FakeApi
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import retrofit2.Response
import javax.inject.Inject

class DefaultFeedRepository @Inject constructor() {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    @Inject
    private lateinit var feedApi: FeedApi

    suspend fun fetchFeeds(category: String, lastFeedId: Long, size: Int): FeedsEntity {
        val requestBody = FeedsRequestDto(lastFeedId = lastFeedId, size = size)
        val result = FakeApi.getFeeds(
            category = if (category == "all") null else category,
            feedsRequestDto = requestBody,
        )

        return result.toData()
            .also {
                _cachedFeeds.addAll(it.feeds)
            }
            .copy(feeds = cachedFeeds)
    }

    suspend fun saveLike(isLikedOfLikedFeed: Boolean, selectedFeedId: Long): Response<Unit> {

        return when (isLikedOfLikedFeed) {
            true -> feedApi.deleteLikes(selectedFeedId)
            false -> feedApi.postLikes(selectedFeedId)
        }
    }

    suspend fun saveRemovedFeed(feedId: Long): Response<Unit> {
        return feedApi.deleteFeed(feedId)
            .also {
                _cachedFeeds.removeIf { it.id == feedId }
            }
    }

    suspend fun saveSpoilerFeed(feedId: Long): Response<Unit> {
        return feedApi.postSpoilerFeed(feedId)
            .also {
                _cachedFeeds.removeIf { it.id == feedId }
            }
    }

    suspend fun saveImpertinenceFeed(feedId: Long): Response<Unit> {
        return feedApi.postImpertinenceFeed(feedId)
            .also {
                _cachedFeeds.removeIf { it.id == feedId }
            }
    }

    fun clearCachedFeeds() {
        if (cachedFeeds.isNotEmpty()) _cachedFeeds.clear()
    }
}
