package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.DefaultFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feed

class GetFeedsUseCase(
    private val defaultFeedRepository: DefaultFeedRepository,
) {
    private var lastFeedId: Long = 0

    suspend operator fun invoke(category: String = DEFAULT_VALUE): List<Feed> {
        val feeds = defaultFeedRepository.getFeeds(
            category = category, lastFeedId = lastFeedId, size = FEEDS_SIZE,
        )

        lastFeedId = feeds.last().id

        return feeds.map { it.toDomain() }
    }

    companion object {
        private const val DEFAULT_VALUE = "all"
        private const val FEEDS_SIZE = 20
    }
}
