package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApi {

    @GET("feeds")
    suspend fun getFeeds(
        @Body feedsRequestDto: FeedsRequestDto,
    ): FeedsResponseDto

    @GET("feeds?")
    suspend fun getFeedsByCategory(
        @Query("category") category: String,
        @Body feedsRequestDto: FeedsRequestDto,
    ): FeedsResponseDto

    @DELETE("feeds/{feedId}")
    suspend fun deleteFeed(
        @Path("feedId") feedId: Long,
    ): Response<Unit>

    @POST("feeds/{feedId}/likes")
    suspend fun postLikes(
        @Path("feedId") feedId: Long,
    ): Response<Unit>

    @DELETE("feeds/{feedId}/likes")
    suspend fun deleteLikes(
        @Path("feedId") feedId: Long,
    ): Response<Unit>

    @POST("feeds/{feedId}/spoiler")
    suspend fun postSpoilerFeed(
        @Path("feedId") feedId: Long,
    ): Response<Unit>

    @POST("feeds/{feedId}/impertinence")
    suspend fun postImpertinenceFeed(
        @Path("feedId") feedId: Long,
    ): Response<Unit>
}
