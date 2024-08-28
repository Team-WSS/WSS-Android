package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.NormalExploreResult
import javax.inject.Inject

class GetNormalExploreResultsUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var previousSearchWord: String = ""
    private var previousPage: Int = INITIAL_PAGE

    suspend operator fun invoke(
        searchWord: String,
        isSearchButtonClick: Boolean,
    ): NormalExploreResult {
        if (isSearchButtonClick && previousSearchWord == searchWord) {
            return ExploreResultEntity(
                resultCount = novelRepository.cachedNormalExploreResult.size.toLong(),
                isLoadable = novelRepository.cachedNormalExploreIsLoadable.value ?: true,
                novels = novelRepository.cachedNormalExploreResult,
            ).toDomain()
        }

        if (isSearchButtonClick) {
            novelRepository.clearCachedNormalExploreResult()
            previousPage = INITIAL_PAGE
        } else {
            previousPage += ADDITIONAL_PAGE_SIZE
        }

        return novelRepository.fetchNormalExploreResult(
            searchWord = searchWord,
            page = previousPage,
            size = REQUEST_SIZE,
        ).toDomain().also {
            previousSearchWord = searchWord
        }
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val ADDITIONAL_PAGE_SIZE = 1
        private const val REQUEST_SIZE = 20
    }
}
