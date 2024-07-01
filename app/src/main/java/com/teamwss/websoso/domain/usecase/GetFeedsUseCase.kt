package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.DefaultFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val defaultFeedRepository: DefaultFeedRepository,
) {
    private var lastFeedId: Long = INITIAL_ID
    private var previousCategory: String = ""

    suspend operator fun invoke(selectedCategory: String): Feeds {
        if (defaultFeedRepository.cachedFeeds.isNotEmpty() && previousCategory != selectedCategory) {
            defaultFeedRepository.clearCachedFeeds()
            lastFeedId = INITIAL_ID
        }

        return defaultFeedRepository.fetchFeeds(
            category = selectedCategory,
            lastFeedId = lastFeedId,
            size = if (lastFeedId == INITIAL_ID) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also {
                lastFeedId = it.feeds.last().id
                previousCategory = selectedCategory
            }
    }

    companion object {
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
