package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.ExploreResult
import javax.inject.Inject

class GetSearchedNovelsUseCase
    @Inject
    constructor(
        private val novelRepository: NovelRepository,
    ) {
        private var page = 0
        private var searchedText = ""

        suspend operator fun invoke(typedText: String = searchedText): ExploreResult {
            if (searchedText != typedText) {
                novelRepository.clearCachedNormalExploreResult()
                page = 0
            }

            return novelRepository
                .fetchNormalExploreResult(
                    searchWord = typedText,
                    page = page,
                    size = if (page == 0) INITIAL_REQUEST_SIZE else ADDITIONAL_REQUEST_SIZE,
                ).toDomain()
                .also {
                    searchedText = typedText
                    page += 1
                }
        }

        companion object {
            private const val INITIAL_REQUEST_SIZE = 20
            private const val ADDITIONAL_REQUEST_SIZE = 10
        }
    }
