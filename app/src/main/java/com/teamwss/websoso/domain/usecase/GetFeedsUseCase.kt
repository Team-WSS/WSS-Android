package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.domain.mapper.FeedMapper.toDomain
import com.teamwss.websoso.domain.model.Feeds
import javax.inject.Inject

class GetFeedsUseCase @Inject constructor(
    private val fakeFeedRepository: FakeFeedRepository
) {

    operator fun invoke(): Feeds {
        return fakeFeedRepository.dummyData.toDomain()
    }
}