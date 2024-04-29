package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.DefaultFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.domain.model.FeedType
import com.teamwss.websoso.domain.model.FeedType.MainFeed
import com.teamwss.websoso.domain.model.FeedType.MyFeed
import com.teamwss.websoso.domain.model.FeedType.NovelFeed

class GetFeedsUseCase(
    private val defaultFeedRepository: DefaultFeedRepository,
) {
    private var lastFeedId: Long = DEFAULT_ID
    private var selectedCategory: String? = NOTHING

    suspend operator fun invoke(
        category: String = DEFAULT_CATEGORY,
        feedType: FeedType
    ): List<Feed> {
        fetchFeedsByFeedType(feedType, if (category == DEFAULT_CATEGORY) null else category)

        defaultFeedRepository.cachedFeeds.apply {
            lastFeedId = if (isNotEmpty()) last().id else DEFAULT_ID // 리스트가 빈 경우, 본 로직이 없으면 터지나?
            return map { it.toDomain() }
        }
    }

    private suspend fun fetchFeedsByFeedType(feedType: FeedType, category: String?) {
        when (feedType) {
            is MainFeed -> {
                checkCategory(category)
                defaultFeedRepository.fetchFeedsByCategory(
                    category = category,
                    lastFeedId = lastFeedId,
                    size = if (lastFeedId == DEFAULT_ID) 30 else 15,
                )
            }

            is MyFeed -> defaultFeedRepository.fetchFeedsByUserId(
                userId = feedType.userId, lastFeedId = lastFeedId, size = 10,
            )


            is NovelFeed -> defaultFeedRepository.fetchFeedsByNovelId(
                novelId = feedType.novelId, lastFeedId = lastFeedId, size = 10,
            )
        }
    }

    private fun checkCategory(category: String?) {
        if (selectedCategory != category) {
            selectedCategory = category
            clear()
        }
    }

    private fun clear() {
        if (defaultFeedRepository.cachedFeeds.isNotEmpty()) {
            defaultFeedRepository.clearCachedFeeds()
            lastFeedId = DEFAULT_ID
        }
    }

    companion object {
        private const val NOTHING: String = ""
        private const val DEFAULT_CATEGORY: String = "전체"
        private const val DEFAULT_ID: Long = 0
    }
}
