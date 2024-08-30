package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetSearchedNovelsUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var page = 0

    suspend operator fun invoke(typedText: String): ExploreResult =
        novelRepository.fetchNormalExploreResult(
            searchWord = typedText,
            page = page,
            size = if (page == 0) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
        ).toDomain()
            .also { page += 1 }

    companion object {
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
