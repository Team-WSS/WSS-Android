package com.into.websoso.feed

import com.into.websoso.data.feed.repository.UpdatedFeedRepository
import com.into.websoso.feed.mapper.toDomain
import com.into.websoso.feed.model.Feed
import com.into.websoso.feed.model.Feeds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdatedGetFeedsUseCase @Inject constructor(
    private val feedRepository: UpdatedFeedRepository,
) {
    val sosoAllFlow: Flow<List<Feed>> = feedRepository.sosoAllFeeds
        .map { list -> list.map { it.toDomain() } }

    val sosoRecommendedFlow: Flow<List<Feed>> = feedRepository.sosoRecommendedFeeds
        .map { list -> list.map { it.toDomain() } }

    /**
     * [Modified] 데이터 로드 트리거
     * - 서버에서 데이터를 가져와 Repository 내부의 HotFlow를 업데이트합니다.
     * - 반환값은 UI의 페이징 상태(isLoadable, lastId) 관리를 위한 메타데이터입니다.
     */
    suspend operator fun invoke(
        feedsOption: String = DEFAULT_OPTION,
        lastFeedId: Long = INITIAL_ID,
    ): Feeds {
        val isFeedRefreshed: Boolean = lastFeedId == INITIAL_ID

        val feedsEntity = feedRepository.fetchFeeds(
            lastFeedId = lastFeedId,
            size = if (isFeedRefreshed) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
            feedsOption = feedsOption,
        )

        return feedsEntity.toDomain()
    }

    companion object {
        private const val DEFAULT_OPTION = "ALL"
        private const val INITIAL_ID: Long = 0
        private const val INITIAL_REQUEST_SIZE = 40
        private const val ADDITIONAL_REQUEST_SIZE = 20
    }
}
