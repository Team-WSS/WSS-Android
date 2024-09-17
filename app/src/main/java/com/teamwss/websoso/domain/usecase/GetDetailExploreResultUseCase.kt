package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetDetailExploreResultUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var previousGenres: List<String>? = null
    private var previousIsCompleted: Boolean? = null
    private var previousNovelRating: Float? = null
    private var previousKeywordIds: List<Int>? = null
    private var previousPage: Int = INITIAL_PAGE

    suspend operator fun invoke(
        genres: List<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: List<Int>?,
        isSearchButtonClick: Boolean,
    ): ExploreResult {

        if (isSearchButtonClick && isCacheValid(genres, isCompleted, novelRating, keywordIds)) {
            return ExploreResultEntity(
                resultCount = novelRepository.cachedNormalExploreResult.size.toLong(),
                isLoadable = novelRepository.cachedDetailExploreIsLoadable,
                novels = novelRepository.cachedDetailExploreResult,
            ).toDomain()
        }

        when (isSearchButtonClick) {
            true -> {
                novelRepository.clearCachedDetailExploreResult()
                previousPage = INITIAL_PAGE
            }

            false -> previousPage += ADDITIONAL_PAGE_SIZE
        }

        return novelRepository.fetchFilteredNovelResult(
            genres = genres,
            isCompleted = isCompleted,
            novelRating = novelRating,
            keywordIds = keywordIds,
            page = previousPage,
            size = REQUEST_SIZE,
        ).toDomain().also {
            previousGenres = genres
            previousIsCompleted = isCompleted
            previousNovelRating = novelRating
            previousKeywordIds = keywordIds
        }
    }

    private fun isCacheValid(
        genres: List<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: List<Int>?,
    ): Boolean {
        return genres?.equals(previousGenres) == true
                && isCompleted == previousIsCompleted
                && novelRating == previousNovelRating
                && keywordIds?.equals(previousKeywordIds) == true
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val ADDITIONAL_PAGE_SIZE = 1
        private const val REQUEST_SIZE = 30
    }
}
