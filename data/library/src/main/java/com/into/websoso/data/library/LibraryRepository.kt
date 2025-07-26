package com.into.websoso.data.library

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.into.websoso.core.database.entity.InDatabaseFilteredNovelEntity
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.library.datasource.FilteredLibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.datasource.MyLibraryFilterLocalDataSource
import com.into.websoso.data.library.mediator.FilteredNovelRemoteMediator
import com.into.websoso.data.library.mediator.NovelRemoteMediator
import com.into.websoso.data.library.model.LibraryFilterParams
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
        private val filteredLibraryLocalDataSource: FilteredLibraryLocalDataSource,
        private val myLibraryFilterLocalDataSource: MyLibraryFilterLocalDataSource,
    ) {
        val myLibraryFilter = myLibraryFilterLocalDataSource.myLibraryFilterFlow

        @OptIn(ExperimentalPagingApi::class)
        fun getLibrary(): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediator = NovelRemoteMediator(
                    userId = accountRepository.userId,
                    libraryLocalDataSource = libraryLocalDataSource,
                    libraryRemoteDataSource = libraryRemoteDataSource,
                ),
                pagingSourceFactory = libraryLocalDataSource::selectAllNovels,
            ).flow.map { pagingData ->
                pagingData.map(InDatabaseNovelEntity::toData)
            }

        @OptIn(ExperimentalPagingApi::class)
        fun getFilteredLibrary(
            readStatuses: List<String>,
            attractivePoints: List<String>,
            isInterested: Boolean,
            novelRating: Float,
            sortCriteria: String,
        ): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediator = FilteredNovelRemoteMediator(
                    userId = accountRepository.userId,
                    libraryRemoteDataSource = libraryRemoteDataSource,
                    filteredLibraryLocalDataSource = filteredLibraryLocalDataSource,
                    isInterested = isInterested,
                    readStatuses = readStatuses,
                    attractivePoints = attractivePoints,
                    novelRating = novelRating,
                    sortCriteria = sortCriteria,
                ),
                pagingSourceFactory = filteredLibraryLocalDataSource::selectAllNovels,
            ).flow.map { pagingData ->
                pagingData.map(InDatabaseFilteredNovelEntity::toData)
            }

        suspend fun updateMyLibraryFilter(
            readStatuses: Map<String, Boolean>? = null,
            attractivePoints: Map<String, Boolean>? = null,
            novelRating: Float? = null,
            isInterested: Boolean? = null,
            sortCriteria: String? = null,
        ) {
            val savedFilter = myLibraryFilter.firstOrNull() ?: LibraryFilterParams()
            val updatedFilter = savedFilter.copy(
                sortCriteria = sortCriteria ?: savedFilter.sortCriteria,
                isInterested = isInterested ?: savedFilter.isInterested,
                readStatuses = readStatuses ?: savedFilter.readStatuses,
                attractivePoints = attractivePoints ?: savedFilter.attractivePoints,
                novelRating = novelRating ?: savedFilter.novelRating,
            )

            myLibraryFilterLocalDataSource.updateMyLibraryFilter(params = updatedFilter)
        }

        companion object {
            private const val PAGE_SIZE = 10
        }
    }
