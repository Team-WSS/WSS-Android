package com.into.websoso.domain.library

import androidx.paging.PagingData
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLibraryUseCase
    @Inject
    constructor(
        private val libraryRepository: LibraryRepository,
    ) {
        operator fun invoke(
            isInterested: Boolean,
            readStatuses: Map<ReadStatus, Boolean>,
            attractivePoints: Map<AttractivePoints, Boolean>,
            novelRating: Float,
        ): Flow<PagingData<NovelEntity>> {
            val isEmptyFilter = readStatuses.values.none { it } &&
                attractivePoints.values.none { it } &&
                novelRating == 0f

            return if (isEmptyFilter) {
                libraryRepository.getLibrary()
            } else {
                libraryRepository.getFilteredLibrary(
                    isInterested = isInterested,
                    readStatuses = readStatuses.toSelectedKeyList { it.name },
                    attractivePoints = attractivePoints.toSelectedKeyList { it.name },
                    novelRating = novelRating,
                )
            }
        }

        private fun <K> Map<K, Boolean>.toSelectedKeyList(toSelectedKeyName: (K) -> String): List<String> =
            this
                .filterValues { it }
                .keys
                .map { toSelectedKeyName(it) }
    }
