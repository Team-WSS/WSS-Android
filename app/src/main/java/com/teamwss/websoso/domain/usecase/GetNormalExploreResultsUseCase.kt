package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.NormalExploreResult
import javax.inject.Inject

class GetNormalExploreResultsUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var previousSearchWord: String = ""

    suspend operator fun invoke(searchWord: String): NormalExploreResult {
        val isSearchWordSwitched: Boolean = previousSearchWord != searchWord

        if ((isSearchWordSwitched) && novelRepository.cachedNormalExploreResult.isNotEmpty())
            novelRepository.clearCachedNormalExploreResult()

        return novelRepository.fetchNormalExploreResult(
            searchWord = searchWord,
            page = INITIAL_PAGE,
            size = INITIAL_REQUEST_SIZE,
        ).toDomain()
            .also { previousSearchWord = searchWord }
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val INITIAL_REQUEST_SIZE = 20
        private const val ADDITIONAL_REQUEST_SIZE = 10
    }
}
