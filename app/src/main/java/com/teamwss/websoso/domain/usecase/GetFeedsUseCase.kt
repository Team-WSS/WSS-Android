package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    private var previousCategory: String = ""

    suspend operator fun invoke(selectedCategory: String, lastFeedId: Long = INITIAL_ID): Feeds {
        val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID
        val isCategorySwitched: Boolean =
            feedRepository.cachedFeeds.isNotEmpty() && previousCategory != selectedCategory

        if (isFeedRefreshed or isCategorySwitched) feedRepository.clearCachedFeeds()

        return feedRepository.fetchFeeds(
            category = selectedCategory,
            lastFeedId = lastFeedId,
            size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also { previousCategory = selectedCategory }
    }

    companion object {
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
