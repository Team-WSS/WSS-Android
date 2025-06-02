package com.into.websoso.data.repository

import android.net.Uri
import com.into.websoso.core.common.util.ImageCompressor
import com.into.websoso.data.mapper.MultiPartMapper
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.CommentsEntity
import com.into.websoso.data.model.FeedEntity
import com.into.websoso.data.model.FeedsEntity
import com.into.websoso.data.model.PopularFeedsEntity
import com.into.websoso.data.model.UserInterestFeedsEntity
import com.into.websoso.data.remote.api.FeedApi
import com.into.websoso.data.remote.request.CommentRequestDto
import com.into.websoso.data.remote.request.FeedRequestDto
import com.into.websoso.data.util.ImageDownloader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository
    @Inject
    constructor(
        private val feedApi: FeedApi,
        private val multiPartMapper: MultiPartMapper,
        private val imageDownloader: ImageDownloader,
        private val imageCompressor: ImageCompressor,
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
            images: List<Uri>,
        ) {
            feedApi.postFeed(
                feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                    target = FeedRequestDto(
                        relevantCategories = relevantCategories,
                        feedContent = feedContent,
                        novelId = novelId,
                        isSpoiler = isSpoiler,
                        isPublic = isPublic,
                    ),
                    partName = PART_NAME_FEED,
                    fileName = "feed.json",
                ),
                images = images.map { multiPartMapper.formatToMultipart(it) },
            )
        }

        suspend fun saveEditedFeed(
            feedId: Long,
            relevantCategories: List<String>,
            feedContent: String,
            novelId: Long?,
            isSpoiler: Boolean,
            isPublic: Boolean,
            images: List<Uri>,
        ) {
            feedApi.putFeed(
                feedId = feedId,
                feedRequestDto = multiPartMapper.formatToMultipart<FeedRequestDto>(
                    target = FeedRequestDto(
                        relevantCategories = relevantCategories,
                        feedContent = feedContent,
                        novelId = novelId,
                        isSpoiler = isSpoiler,
                        isPublic = isPublic,
                    ),
                    partName = "feed",
                    fileName = "feed.json",
                ),
                images = images.map { multiPartMapper.formatToMultipart(it) },
            )
        }

        suspend fun fetchFeed(feedId: Long): FeedEntity = feedApi.getFeed(feedId).toData()

        suspend fun fetchPopularFeeds(): PopularFeedsEntity = feedApi.getPopularFeeds().toData()

        suspend fun fetchUserInterestFeeds(): UserInterestFeedsEntity = feedApi.getUserInterestFeeds().toData()

        suspend fun saveRemovedFeed(feedId: Long) {
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

        suspend fun downloadImage(imageUrl: String): Result<Uri?> = imageDownloader.formatImageToUri(imageUrl)

        suspend fun compressImages(imageUris: List<Uri>): List<Uri> = imageCompressor.compressUris(imageUris)

        companion object {
            private const val PART_NAME_FEED: String = "feed"
            private const val FILE_NAME_FEED: String = "feed.json"
        }
    }
