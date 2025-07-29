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
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class UserLibraryRepository(
    private val userId: Long,
    private val filterRepository: FilterRepository,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
) : LibraryRepository {
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

    interface Factory {
        fun create(userId: Long): LibraryRepository
    }

    @ViewModelScoped
    class UserLibraryRepositoryFactory
        @Inject
        constructor(
            private val libraryRemoteDataSource: LibraryRemoteDataSource,
            private val filterRepository: FilterRepository,
        ) : Factory {
            override fun create(userId: Long): LibraryRepository =
                UserLibraryRepository(
                    userId = userId,
                    filterRepository = filterRepository,
                    libraryRemoteDataSource = libraryRemoteDataSource,
                )
        }
}
