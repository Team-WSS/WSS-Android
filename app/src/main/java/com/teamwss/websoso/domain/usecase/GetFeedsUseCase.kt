package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feed

class GetFeedsUseCase(
    private val fakeFeedRepository: FakeFeedRepository
) {

    operator fun invoke(): List<Feed> {
        return fakeFeedRepository.dummyFeedData.map { it.toDomain() }
    }
}
