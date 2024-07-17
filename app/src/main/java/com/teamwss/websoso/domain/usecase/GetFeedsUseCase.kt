package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    private var lastFeedId: Long = INITIAL_ID
    private var previousCategory: String = ""

    suspend operator fun invoke(selectedCategory: String): Feeds {
        if (feedRepository.cachedFeeds.isNotEmpty() && previousCategory != selectedCategory) {
            feedRepository.clearCachedFeeds()
            lastFeedId = INITIAL_ID
        }

        return feedRepository.fetchFeeds(
            category = selectedCategory,
            lastFeedId = lastFeedId,
            size = if (lastFeedId == INITIAL_ID) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also {
                if (it.feeds.isNotEmpty()) lastFeedId = it.feeds.last().id
                previousCategory = selectedCategory
            }
    }

    companion object {
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
