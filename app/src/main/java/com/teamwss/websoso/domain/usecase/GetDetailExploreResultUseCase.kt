package com.teamwss.websoso.domain.usecase

import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.domain.mapper.toDomain
import com.teamwss.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetDetailExploreResultUseCase @Inject constructor(
    private val novelRepository: NovelRepository,
) {
    private var previousPage: Int = INITIAL_PAGE

    suspend operator fun invoke(
        genres: Array<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: Array<Int>?,
        isSearchButtonClick: Boolean,
    ): ExploreResult {

        when (isSearchButtonClick) {
            true -> previousPage = INITIAL_PAGE
            false -> previousPage += ADDITIONAL_PAGE_SIZE
        }

        return novelRepository.fetchFilteredNovelResult(
            genres = genres,
            isCompleted = isCompleted,
            novelRating = novelRating,
            keywordIds = keywordIds,
            page = previousPage,
            size = REQUEST_SIZE,
        ).toDomain()
    }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val ADDITIONAL_PAGE_SIZE = 1
        private const val REQUEST_SIZE = 20
    }
}