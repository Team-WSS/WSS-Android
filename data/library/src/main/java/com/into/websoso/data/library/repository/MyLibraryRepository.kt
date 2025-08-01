package com.into.websoso.data.library.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.core.common.extensions.isCloseTo
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.filter.model.DEFAULT_NOVEL_RATING
import com.into.websoso.data.filter.model.LibraryFilter
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.LibraryRepository.Companion.PAGE_SIZE
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.paging.LibraryRemoteMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MyLibraryRepository
    @Inject
    constructor(
        filterRepository: FilterRepository,
        private val accountRepository: AccountRepository,
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) : LibraryRepository {
        @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
        override val libraryFlow: Flow<PagingData<NovelEntity>> =
            filterRepository.filterFlow
                .flatMapLatest { filter ->
                    Pager(
                        config = PagingConfig(pageSize = PAGE_SIZE),
                        remoteMediator = LibraryRemoteMediator(
                            getNovels = { lastUserNovelId -> getUserNovels(lastUserNovelId, filter) },
                            deleteAllNovels = libraryLocalDataSource::deleteAllNovels,
                            insertNovels = libraryLocalDataSource::insertNovels,
                        ),
                        pagingSourceFactory = libraryLocalDataSource::selectAllNovels,
                    ).flow
                }

        suspend fun deleteAllNovels() {
            libraryLocalDataSource.deleteAllNovels()
        }

        private suspend fun getUserNovels(
            lastUserNovelId: Long,
            libraryFilter: LibraryFilter,
        ) = runCatching {
            libraryRemoteDataSource.getUserNovels(
                userId = accountRepository.userId,
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
