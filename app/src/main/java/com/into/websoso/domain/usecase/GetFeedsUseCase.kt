package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    private var previousCategory: String = ""

    suspend operator fun invoke(selectedCategory: String, lastFeedId: Long = INITIAL_ID): Feeds {
        val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID
        val isCategorySwitched: Boolean = previousCategory != selectedCategory
        if ((isFeedRefreshed || isCategorySwitched) && feedRepository.cachedFeeds.isNotEmpty())
            feedRepository.clearCachedFeeds()

        return feedRepository.fetchFeeds(
            category = selectedCategory,
            lastFeedId = lastFeedId,
            size = if (isFeedRefreshed or isCategorySwitched) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also { previousCategory = selectedCategory }
    }

    companion object {
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
