package com.into.websoso.data.library.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.filter.FilterRepository
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
import kotlinx.coroutines.flow.flatMapLatest

internal class UserLibraryRepository
    @AssistedInject
    constructor(
        filterRepository: FilterRepository,
        @Assisted private val userId: Long,
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
    ) : LibraryRepository {
        @AssistedFactory
        interface Factory {
            fun create(userId: Long): UserLibraryRepository
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override val libraryFlow: Flow<PagingData<NovelEntity>> =
            filterRepository.filterFlow
                .flatMapLatest { filter ->
                    Pager(
                        config = PagingConfig(pageSize = PAGE_SIZE),
                        pagingSourceFactory = {
                            LibraryPagingSource(
                                userId = userId,
                                libraryRemoteDataSource = libraryRemoteDataSource,
                                filterParams = filter,
                            )
                        },
                    ).flow
                }
    }
