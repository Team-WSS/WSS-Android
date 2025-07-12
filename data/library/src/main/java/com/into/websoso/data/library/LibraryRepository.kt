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
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toData
import kotlinx.coroutines.flow.Flow
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
                config = PagingConfig(pageSize = 20),
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
            isInterested: Boolean,
            readStatuses: List<String>,
            attractivePoints: List<String>,
            novelRating: Float?,
        ): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(pageSize = 40),
                remoteMediator = FilteredNovelRemoteMediator(
                    userId = accountRepository.userId,
                    libraryRemoteDataSource = libraryRemoteDataSource,
                    filteredLibraryLocalDataSource = filteredLibraryLocalDataSource,
                    isInterested = isInterested,
                    readStatuses = readStatuses,
                    attractivePoints = attractivePoints,
                    novelRating = novelRating,
                ),
                pagingSourceFactory = filteredLibraryLocalDataSource::selectAllNovels,
            ).flow.map { pagingData ->
                pagingData.map(InDatabaseFilteredNovelEntity::toData)
            }

        suspend fun updateMyLibraryFilter(
            readStatuses: Map<String, Boolean>,
            attractivePoints: Map<String, Boolean>,
            novelRating: Float,
        ) {
            myLibraryFilterLocalDataSource.updateMyLibraryFilter(
                readStatuses = readStatuses,
                attractivePoints = attractivePoints,
                novelRating = novelRating,
            )
        }
    }
