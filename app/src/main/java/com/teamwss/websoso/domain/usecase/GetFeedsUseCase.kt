package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.DefaultFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feeds

class GetFeedsUseCase(
    private val defaultFeedRepository: DefaultFeedRepository,
) {
    private var lastFeedId: Long = DEFAULT_ID
    private var selectedCategory: String? = NOTHING

    suspend operator fun invoke(
        category: String = DEFAULT_CATEGORY,
    ): Feeds {
        checkCategory(
            if (category == DEFAULT_CATEGORY) null else category
        )

        return defaultFeedRepository.fetchFeeds(
            category = selectedCategory,
            lastFeedId = lastFeedId,
            size = if (lastFeedId == DEFAULT_ID) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also {
                lastFeedId = it.feeds.last().id
            }
    }

    private fun checkCategory(category: String?) {
        if (selectedCategory != category) {
            selectedCategory = category
            clearFeedInfo()
        }
    }

    private fun clearFeedInfo() {
        if (defaultFeedRepository.cachedFeeds.isNotEmpty()) {
            defaultFeedRepository.clearCachedFeeds()
            lastFeedId = DEFAULT_ID
        }
    }

    companion object {
        private const val NOTHING: String = ""
        private const val DEFAULT_CATEGORY: String = "전체"
        private const val DEFAULT_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
