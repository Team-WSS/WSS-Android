package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.FakeNovelDetailRepository
import com.teamwss.websoso.domain.mapper.toDomain
import javax.inject.Inject

class GetNovelDetailUseCase
    @Inject
    constructor(
        private val novelDetailRepository: FakeNovelDetailRepository,
    ) {
        suspend operator fun invoke(novelId: Long) = novelDetailRepository.getNovelDetail(novelId).toDomain()
    }
