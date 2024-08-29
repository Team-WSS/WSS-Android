package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetDetailExploreResultUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var previousGenres: Array<String>? = null
    private var previousIsCompleted: Boolean? = null
    private var previousNovelRating: Float? = null
    private var previousKeywordIds: Array<Int>? = null
    private var previousPage: Int = INITIAL_PAGE

    suspend operator fun invoke(
        genres: Array<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: Array<Int>?,
        isSearchButtonClick: Boolean,
    ): ExploreResult {

        if (isSearchButtonClick && shouldUseCache(genres, isCompleted, novelRating, keywordIds)) {
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

    private fun shouldUseCache(
        genres: Array<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: Array<Int>?
    ): Boolean {
        return genres.contentEquals(previousGenres)
                && isCompleted == previousIsCompleted
                && novelRating == previousNovelRating
                && keywordIds.contentEquals(previousKeywordIds)
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val ADDITIONAL_PAGE_SIZE = 1
        private const val REQUEST_SIZE = 20
    }
}