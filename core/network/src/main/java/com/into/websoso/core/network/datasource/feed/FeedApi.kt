package com.into.websoso.core.network.datasource.feed

import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

interface FeedApi {
    @GET("feeds")
    suspend fun getFeeds(
        @Query("category") category: String?,
        @Query("feedsOption") feedsOption: String,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): FeedsResponseDto

    @DELETE("feeds/{feedId}")
    suspend fun deleteFeed(
        @Path("feedId") feedId: Long,
    )

    @POST("feeds/{feedId}/spoiler")
    suspend fun postSpoilerFeed(
        @Path("feedId") feedId: Long,
    )

    @POST("feeds/{feedId}/impertinence")
    suspend fun postImpertinenceFeed(
        @Path("feedId") feedId: Long,
    )

    @POST("feeds/{feedId}/likes")
    suspend fun postLikes(
        @Path("feedId") feedId: Long,
    )

    @DELETE("feeds/{feedId}/likes")
    suspend fun deleteLikes(
        @Path("feedId") feedId: Long,
    )
}

@Module
@InstallIn(SingletonComponent::class)
internal object FeedApiModule {
    @Provides
    @Singleton
    internal fun provideFeedApi(retrofit: Retrofit): FeedApi = retrofit.create(FeedApi::class.java)
}
