package com.into.websoso.domain.library

import androidx.paging.PagingData
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.SortType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNovelUseCase
    @Inject
    constructor(
        private val libraryRepository: LibraryRepository,
    ) {
        operator fun invoke(
            userId: Long = 184,
            lastUserNovelId: Long = INITIAL_LAST_USER_NOVEL_ID,
            size: Int = 60,
            sortCriteria: SortType = SortType.RECENT,
            isInterest: Boolean? = null,
            readStatuses: List<String>? = null,
            attractivePoints: List<String>? = null,
            novelRating: Float? = null,
            query: String? = null,
        ): Flow<PagingData<NovelEntity>> =
            libraryRepository.getUserLibrary(
                userId = userId,
                lastUserNovelId = lastUserNovelId,
                size = size,
                sortCriteria = sortCriteria.name,
                isInterest = isInterest,
                readStatuses = readStatuses,
                attractivePoints = attractivePoints,
                novelRating = novelRating,
                query = query,
            )

        companion object {
            private const val INITIAL_LAST_USER_NOVEL_ID: Long = 0
        }
    }
