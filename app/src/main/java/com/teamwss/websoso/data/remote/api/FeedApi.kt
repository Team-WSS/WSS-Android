package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import com.teamwss.websoso.data.remote.response.CommentsResponseDto
import com.teamwss.websoso.data.remote.response.FeedResponseDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto
import com.teamwss.websoso.data.remote.response.PopularFeedsResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApi {

    @GET("feeds?")
    suspend fun getFeeds(
        @Query("category") category: String?,
        @Body feedsRequestDto: FeedsRequestDto,
    ): FeedsResponseDto

    @GET("feeds/{feedId}")
    suspend fun getFeed(
        @Path("feedId") feedId: Long,
    ): FeedResponseDto

    @GET("feeds/{feedId}/comments")
    suspend fun getComments(
        @Path("feedId") feedId: Long,
    ): CommentsResponseDto

    @DELETE("feeds/{feedId}")
    suspend fun deleteFeed(
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

    @POST("feeds/{feedId}/spoiler")
    suspend fun postSpoilerFeed(
        @Path("feedId") feedId: Long,
    )

    @POST("feeds/{feedId}/impertinence")
    suspend fun postImpertinenceFeed(
        @Path("feedId") feedId: Long,
    )

    @GET("feeds/popular")
    suspend fun getPopularFeeds(): PopularFeedsResponseDto
}
