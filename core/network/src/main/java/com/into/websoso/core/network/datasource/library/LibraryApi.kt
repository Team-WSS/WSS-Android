package com.into.websoso.core.network.datasource.library

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
    @GET("users/{userId}/novels")
    suspend fun getUserNovels(
        @Path("userId") userId: Long,
        @Query("lastUserNovelId") lastUserNovelId: Long,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
        @Query("isInterest") isInterest: Boolean?,
        @Query("readStatuses") readStatuses: List<String>?,
        @Query("attractivePoints") attractivePoints: List<String>?,
        @Query("novelRating") novelRating: Float?,
        @Query("query") query: String?,
    ): UserNovelsResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object LibraryApiModule {
    @Provides
    @Singleton
    internal fun provideLibraryApi(retrofit: Retrofit): LibraryApi = retrofit.create(LibraryApi::class.java)
}
