package com.into.websoso.data.feed.repository

import com.into.websoso.core.network.datasource.feed.FeedApi
import com.into.websoso.data.feed.mapper.toData
import com.into.websoso.data.feed.model.FeedEntity
import com.into.websoso.data.feed.model.FeedsEntity
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val feedApi: FeedApi,
    private val libraryLocalDataSource: LibraryLocalDataSource,
) {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    suspend fun fetchFeeds(
        lastFeedId: Long,
        size: Int,
        feedsOption: String,
    ): FeedsEntity =
        feedApi
            .getFeeds(
                feedsOption = feedsOption,
                category = null,
                lastFeedId = lastFeedId,
                size = size,
            ).toData()
            .also { _cachedFeeds.addAll(it.feeds) }
            .copy(feeds = cachedFeeds)

    suspend fun saveRemovedFeed(
        feedId: Long,
        novelId: Long?,
        content: String,
    ) {
        runCatching {
            feedApi.deleteFeed(feedId)
        }.onSuccess {
            _cachedFeeds.removeIf { it.id == feedId }

            val novel = novelId?.let { id ->
                libraryLocalDataSource.selectNovelByNovelId(id)
            }

            if (novel != null) {
                val updatedNovel = novel.copy(myFeeds = novel.myFeeds.filterNot { it == content })
                libraryLocalDataSource.insertNovel(updatedNovel)
            }
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
