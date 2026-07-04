package com.into.websoso.core.network.datasource.library

import com.into.websoso.core.network.datasource.library.model.response.UserNovelKeywordsResponseDto
import com.into.websoso.core.network.datasource.library.model.response.UserNovelsResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

internal interface LibraryApi {
    @GET("users/{userId}/novels/v2")
    suspend fun getUserNovels(
        @Path("userId") userId: Long,
        @Query("size") size: Int,
        @Query("cursor") cursor: String?,
        @Query("sortType") sortType: String,
        @Query("isInterest") isInterest: Boolean?,
        @Query("readStatuses") readStatuses: List<String>?,
        @Query("genres") genres: List<String>?,
        @Query("isComplete") isComplete: Boolean?,
        @Query("ratingMin") ratingMin: Float?,
        @Query("ratingMax") ratingMax: Float?,
        @Query("unratedOnly") unratedOnly: Boolean?,
        @Query("attractivePoints") attractivePoints: List<String>?,
        @Query("keywords") keywords: List<String>?,
    ): UserNovelsResponseDto

    @GET("users/{userId}/novels/keywords")
    suspend fun getUserNovelKeywords(
        @Path("userId") userId: Long,
    ): UserNovelKeywordsResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object LibraryApiModule {
    @Provides
    @Singleton
    internal fun provideLibraryApi(retrofit: Retrofit): LibraryApi = retrofit.create(LibraryApi::class.java)
}
