package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.CommentsEntity
import com.teamwss.websoso.data.model.FeedEntity
import com.teamwss.websoso.data.model.FeedsEntity
import com.teamwss.websoso.data.model.PopularFeedsEntity
import com.teamwss.websoso.data.model.UserInterestFeedsEntity
import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.request.CommentRequestDto
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val feedApi: FeedApi,
) {
    private val _cachedFeeds: MutableList<FeedEntity> = mutableListOf()
    val cachedFeeds: List<FeedEntity> get() = _cachedFeeds.toList()

    fun clearCachedFeeds() {
        if (cachedFeeds.isNotEmpty()) _cachedFeeds.clear()
    }

    suspend fun fetchFeeds(category: String, lastFeedId: Long, size: Int): FeedsEntity =
        feedApi.getFeeds(
            category = if (category == "all") null else category,
            lastFeedId = lastFeedId,
            size = size,
        ).toData()
            .also { _cachedFeeds.addAll(it.feeds) }
            .copy(feeds = cachedFeeds)

    suspend fun fetchFeed(feedId: Long): FeedEntity = feedApi.getFeed(feedId).toData()

    suspend fun fetchPopularFeeds(): PopularFeedsEntity {
        return feedApi.getPopularFeeds().toData()
    }

    suspend fun fetchUserInterestFeeds(): UserInterestFeedsEntity {
        return feedApi.getUserInterestFeeds().toData()
    }

    suspend fun saveRemovedFeed(feedId: Long) {
        feedApi.deleteFeed(feedId).also { _cachedFeeds.removeIf { it.id == feedId } }
    }

    suspend fun saveSpoilerFeed(feedId: Long) {
        feedApi.postSpoilerFeed(feedId)
    }

    suspend fun saveImpertinenceFeed(feedId: Long) {
        feedApi.postImpertinenceFeed(feedId)
    }

    suspend fun saveLike(isLikedOfLikedFeed: Boolean, selectedFeedId: Long) {
        when (isLikedOfLikedFeed) {
            true -> feedApi.deleteLikes(selectedFeedId)
            false -> feedApi.postLikes(selectedFeedId)
        }
    }

    suspend fun fetchComments(feedId: Long): CommentsEntity = feedApi.getComments(feedId).toData()

    suspend fun saveComment(feedId: Long, comment: String) {
        feedApi.postComment(feedId, CommentRequestDto(comment))
    }

    suspend fun saveModifiedComment(feedId: Long, commentId: Long, comment: String) {
        feedApi.putComment(feedId, commentId, CommentRequestDto(comment))
    }

    suspend fun deleteComment(feedId: Long, commentId: Long) {
        feedApi.deleteComment(feedId, commentId)
    }

    suspend fun saveSpoilerComment(feedId: Long, commentId: Long) {
        feedApi.postSpoilerComment(feedId, commentId)
    }

    suspend fun saveImpertinenceComment(feedId: Long, commentId: Long) {
        feedApi.postImpertinenceComment(feedId, commentId)
    }
}
