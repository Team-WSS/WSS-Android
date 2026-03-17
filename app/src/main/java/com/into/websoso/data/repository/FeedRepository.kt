package com.into.websoso.data.repository

import com.into.websoso.data.mapper.MultiPartMapper
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.FeedsEntity
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.UserInterestFeedsEntity
import com.into.websoso.data.remote.api.FeedApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository
    @Inject
    constructor(
        private val feedApi: FeedApi,
    ) {
        private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
        val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

        fun clearCachedFeeds() {
            if (cachedFeeds.isNotEmpty()) _cachedFeeds.clear()
        }

        suspend fun fetchFeeds(
            category: String,
            lastFeedId: Long,
            size: Int,
        ): FeedsEntity =
            feedApi
                .getFeeds(
                    category = if (category == "all") null else category,
                    lastFeedId = lastFeedId,
                    size = size,
                ).toData()
                .also { _cachedFeeds.addAll(it.feeds) }
                .copy(feeds = cachedFeeds)

        suspend fun fetchPopularFeeds(): PopularFeedsEntity = feedApi.getPopularFeeds().toData()

        suspend fun fetchUserInterestFeeds(): UserInterestFeedsEntity = feedApi.getUserInterestFeeds().toData()

        suspend fun saveRemovedFeed(feedId: Long) {
            runCatching {
                feedApi.deleteFeed(feedId)
            }.onSuccess {
                _cachedFeeds.removeIf { it.id == feedId }
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
