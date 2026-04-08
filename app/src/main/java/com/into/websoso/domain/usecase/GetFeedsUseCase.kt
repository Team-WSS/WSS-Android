package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase
    @Inject
    constructor(
        private val feedRepository: FeedRepository,
    ) {
        suspend operator fun invoke(
            lastFeedId: Long = INITIAL_ID,
        ): Feeds {
            val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID
            if (isFeedRefreshed && feedRepository.cachedFeeds.isNotEmpty()) {
                feedRepository.clearCachedFeeds()
            }

            return feedRepository
                .fetchFeeds(
                    lastFeedId = lastFeedId,
                    size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
                ).toDomain()
        }

        companion object {
            private const val INITIAL_ID: Long = 0
            private const val INITIAL_REQUEST_SIZE = 20
            private const val ADDITIONAL_REQUEST_SIZE = 10
        }
    }
