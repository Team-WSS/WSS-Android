package com.into.websoso.core.network.datasource.library

import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.UserNovelsEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryDataSource
    @Inject
    constructor(
        private val libraryApi: LibraryApi,
    ) : LibraryRemoteDataSource {
        override suspend fun getUserNovels(
            userId: Long,
            lastUserNovelId: Long,
            size: Int,
            sortType: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            query: String?,
        ): UserNovelsEntity =
            libraryApi
                .getUserNovels(
                    userId = userId,
                    lastUserNovelId = lastUserNovelId,
                    size = size,
                    sortType = sortType,
                    isInterest = isInterest,
                    readStatuses = readStatuses,
                    attractivePoints = attractivePoints,
                    novelRating = novelRating,
                    query = query,
                ).toData()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryRemoteDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryRemoteDataSource
}
