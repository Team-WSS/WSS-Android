package com.into.websoso.data.library.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.filter.FilterRepository
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.library.LibraryRepository.Companion.PAGE_SIZE
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toData
import com.into.websoso.data.library.paging.LibraryRemoteMediator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class MyLibraryRepository
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
                            userId = accountRepository.userId,
                            libraryRemoteDataSource = libraryRemoteDataSource,
                            libraryLocalDataSource = libraryLocalDataSource,
                            libraryFilter = filter,
                        ),
                        pagingSourceFactory = libraryLocalDataSource::selectAllNovels,
                    ).flow.map { pagingData ->
                        pagingData.map(InDatabaseNovelEntity::toData)
                    }
                }
    }
