package com.into.websoso.feed

import com.into.websoso.data.feed.repository.FeedRepository
import com.into.websoso.feed.mapper.toDomain
import com.into.websoso.feed.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    suspend operator fun invoke(
        feedsOption: String = DEFAULT_OPTION,
        lastFeedId: Long = INITIAL_ID,
    ): Feeds {
        val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID

        return feedRepository
            .fetchFeeds(
                lastFeedId = lastFeedId,
                size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
            ).toDomain()
    }

    companion object {
        private const val DEFAULT_OPTION = "ALL"
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 40
        private const val ADDITIONAL_REQUEST_SIZE = 20
    }
}
