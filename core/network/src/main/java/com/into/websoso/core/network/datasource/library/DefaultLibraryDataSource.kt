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
            sortCriteria: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            query: String?,
            updatedSince: String?,
        ): UserNovelsEntity =
            libraryApi
                .getUserNovels(
                    userId = userId,
                    lastUserNovelId = lastUserNovelId,
                    size = size,
                    sortCriteria = sortCriteria,
                    isInterest = isInterest,
                    readStatuses = readStatuses,
                    attractivePoints = attractivePoints,
                    novelRating = novelRating,
                    query = query,
                    updatedSince = null,
                ).toData()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryRemoteDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryRemoteDataSource
}
