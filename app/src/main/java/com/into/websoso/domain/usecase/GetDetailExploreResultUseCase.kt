package com.into.websoso.domain.usecase

import com.into.websoso.data.model.ExploreResultEntity
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetDetailExploreResultUseCase
    @Inject
    constructor(
        private val novelRepository: NovelRepository,
    ) {
        private var previousGenres: List<String>? = null
        private var previousIsCompleted: Boolean? = null
        private var previousNovelRatingStart: Float = RATING_MIN_DEFAULT
        private var previousNovelRatingEnd: Float = RATING_MAX_DEFAULT
        private var previousKeywordIds: List<Int>? = null
        private var previousPage: Int = INITIAL_PAGE

        suspend operator fun invoke(
            genres: List<String>?,
            isCompleted: Boolean?,
            novelRatingStart: Float,
            novelRatingEnd: Float,
            keywordIds: List<Int>?,
            isSearchButtonClick: Boolean,
        ): ExploreResult {
            if (isSearchButtonClick && isCacheValid(genres, isCompleted, novelRatingStart, novelRatingEnd, keywordIds)) {
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

            return novelRepository
                .fetchFilteredNovelResult(
                    genres = genres,
                    isCompleted = isCompleted,
                    novelRatingStart = novelRatingStart,
                    novelRatingEnd = novelRatingEnd,
                    keywordIds = keywordIds,
                    page = previousPage,
                    size = REQUEST_SIZE,
                ).toDomain()
                .also {
                    previousGenres = genres
                    previousIsCompleted = isCompleted
                    previousNovelRatingStart = novelRatingStart
                    previousNovelRatingEnd = novelRatingEnd
                    previousKeywordIds = keywordIds
                }
        }

        private fun isCacheValid(
            genres: List<String>?,
            isCompleted: Boolean?,
            novelRatingStart: Float,
            novelRatingEnd: Float,
            keywordIds: List<Int>?,
        ): Boolean =
            genres?.equals(previousGenres) == true &&
                isCompleted == previousIsCompleted &&
                novelRatingStart == previousNovelRatingStart &&
                novelRatingEnd == previousNovelRatingEnd &&
                keywordIds?.equals(previousKeywordIds) == true

        companion object {
            private const val INITIAL_PAGE = 0
            private const val ADDITIONAL_PAGE_SIZE = 1
            private const val REQUEST_SIZE = 30
            private const val RATING_MIN_DEFAULT = 0.0f
            private const val RATING_MAX_DEFAULT = 5.0f
        }
    }
