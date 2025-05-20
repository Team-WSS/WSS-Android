package com.into.websoso.core.network.datasource.library

import com.into.websoso.core.network.datasource.library.model.response.UserLibraryResponseDto
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
    suspend fun getUserLibrary(
        @Path("userId") userId: Long,
    ): UserLibraryResponseDto

    @GET("users/{userId}/novels")
    suspend fun getUserStorage(
        @Path("userId") userId: Long,
        @Query("readStatus") readStatus: String,
        @Query("lastUserNovelId") lastUserNovelId: Long,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
    ): UserLibraryResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object LibraryApiModule {
    @Provides
    @Singleton
    internal fun provideLibraryApi(retrofit: Retrofit): LibraryApi = retrofit.create(LibraryApi::class.java)
}
