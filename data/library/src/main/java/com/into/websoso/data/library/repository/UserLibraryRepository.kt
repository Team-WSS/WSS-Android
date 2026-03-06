package com.into.websoso.data.library.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.core.common.extensions.isCloseTo
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.model.DEFAULT_NOVEL_RATING
import com.into.websoso.data.filter.model.LibraryFilter
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.LibraryRepository.Companion.PAGE_SIZE
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.paging.LibraryPagingSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update

internal class UserLibraryRepository
@AssistedInject
constructor(
    private val filterRepository: FilterRepository,
    @Assisted private val userId: Long,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
) : LibraryRepository {
    private var _novelTotalCount: MutableStateFlow<Long> = MutableStateFlow(0)
    override val novelTotalCount: Flow<Long> = _novelTotalCount.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(userId: Long): UserLibraryRepository
    }

    /**
     * Room 캐싱 없이 서버와 직접 통신하며, 필터 변경 시 스트림을 재생성합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLibraryFlow(): Flow<PagingData<NovelEntity>> =
        filterRepository.filterFlow
            .flatMapLatest { currentFilter ->
                Pager(
                    config = PagingConfig(
                        pageSize = PAGE_SIZE,
                        enablePlaceholders = false,
                    ),
                    pagingSourceFactory = {
                        LibraryPagingSource(
                            getNovels = { lastUserNovelId ->
                                getUserNovels(lastUserNovelId, currentFilter).also { result ->
                                    _novelTotalCount.update {
                                        result.getOrNull()?.userNovelCount ?: 0
                                    }
                                }
                            },
                        )
                    },
                ).flow
            }

    private suspend fun getUserNovels(
        lastUserNovelId: Long,
        libraryFilter: LibraryFilter,
    ) = runCatching {
        libraryRemoteDataSource.getUserNovels(
            userId = userId,
            lastUserNovelId = lastUserNovelId,
            size = PAGE_SIZE,
            sortCriteria = libraryFilter.sortCriteria,
            isInterest = if (!libraryFilter.isInterested) null else true,
            readStatuses = libraryFilter.readStatuses.ifEmpty { null },
            attractivePoints = libraryFilter.attractivePoints.ifEmpty { null },
            novelRating = if (libraryFilter.novelRating.isCloseTo(DEFAULT_NOVEL_RATING)) {
                null
            } else {
                libraryFilter.novelRating
            },
            query = null,
            updatedSince = null,
        )
    }
}
