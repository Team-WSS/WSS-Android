package com.into.websoso.data.repository

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.CommentsEntity
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.FeedsEntity
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.UserInterestFeedsEntity
import com.into.websoso.data.remote.api.FeedApi
import com.into.websoso.data.remote.request.CommentRequestDto
import com.into.websoso.data.remote.request.FeedRequestDto
import javax.inject.Inject

class FeedRepository
    @Inject
    constructor(
        private val feedApi: FeedApi,
        private val libraryLocalDataSource: LibraryLocalDataSource,
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

        suspend fun saveFeed(
            relevantCategories: List<String>,
            feedContent: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
        ) {
            val novel = novelId?.let { id ->
                libraryLocalDataSource.selectNovelByNovelId(id)
            }

            if (novel != null) {
                val updatedNovel = novel.copy(myFeeds = listOf(feedContent) + novel.myFeeds)
                libraryLocalDataSource.insertNovel(updatedNovel)
            }

            feedApi.postFeed(
                FeedRequestDto(
                    relevantCategories = relevantCategories,
                    feedContent = feedContent,
                    novelId = novelId,
                    isSpoiler = isSpoiler,
                    isPublic = isPublic,
                ),
            )
        }

        suspend fun saveEditedFeed(
            feedId: Long,
            relevantCategories: List<String>,
            editedFeed: String,
            legacyFeed: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
        ) {
            val novel = novelId?.let { id ->
                libraryLocalDataSource.selectNovelByNovelId(id)
            }

            if (novel != null) {
                val updatedNovel = novel.copy(
                    myFeeds = novel.myFeeds.map { currentFeed ->
                        if (currentFeed == legacyFeed) editedFeed else currentFeed
                    },
                )
                libraryLocalDataSource.insertNovel(updatedNovel)
            }

            feedApi.putFeed(
                feedId,
                FeedRequestDto(
                    relevantCategories = relevantCategories,
                    feedContent = editedFeed,
                    novelId = novelId,
                    isSpoiler = isSpoiler,
                    isPublic = isPublic,
                ),
            )
        }

        suspend fun fetchFeed(feedId: Long): FeedEntity = feedApi.getFeed(feedId).toData()

        suspend fun fetchPopularFeeds(): PopularFeedsEntity = feedApi.getPopularFeeds().toData()

        suspend fun fetchUserInterestFeeds(): UserInterestFeedsEntity = feedApi.getUserInterestFeeds().toData()

        suspend fun saveRemovedFeed(
            feedId: Long,
            novelId: Long?,
            content: String,
        ) {
            val novel = novelId?.let { id ->
                libraryLocalDataSource.selectNovelByNovelId(id)
            }

            if (novel != null) {
                val updatedNovel = novel.copy(myFeeds = novel.myFeeds.filterNot { it == content })
                libraryLocalDataSource.insertNovel(updatedNovel)
            }

            feedApi.deleteFeed(feedId).also { _cachedFeeds.removeIf { it.id == feedId } }
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

        suspend fun fetchComments(feedId: Long): CommentsEntity = feedApi.getComments(feedId).toData()

        suspend fun saveComment(
            feedId: Long,
            comment: String,
        ) {
            feedApi.postComment(feedId, CommentRequestDto(comment))
        }

        suspend fun saveModifiedComment(
            feedId: Long,
            commentId: Long,
            comment: String,
        ) {
            feedApi.putComment(feedId, commentId, CommentRequestDto(comment))
        }

        suspend fun deleteComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.deleteComment(feedId, commentId)
        }

        suspend fun saveSpoilerComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.postSpoilerComment(feedId, commentId)
        }

        suspend fun saveImpertinenceComment(
            feedId: Long,
            commentId: Long,
        ) {
            feedApi.postImpertinenceComment(feedId, commentId)
        }
    }
