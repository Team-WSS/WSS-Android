package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.UserNovelsEntity

interface LibraryRemoteDataSource {
    suspend fun getUserNovels(
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
    ): UserNovelsEntity
}
