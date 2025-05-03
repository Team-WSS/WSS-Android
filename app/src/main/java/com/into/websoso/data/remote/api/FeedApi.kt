package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.CommentRequestDto
import com.into.websoso.data.remote.request.FeedRequestDto
import com.into.websoso.data.remote.response.CommentsResponseDto
import com.into.websoso.data.remote.response.FeedDetailResponseDto
import com.into.websoso.data.remote.response.FeedsResponseDto
import com.into.websoso.data.remote.response.PopularFeedsResponseDto
import com.into.websoso.data.remote.response.UserInterestFeedsResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FeedApi {
    @GET("feeds")
    suspend fun getFeeds(
        @Query("category") category: String?,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): FeedsResponseDto

    @POST("feeds")
    suspend fun postFeed(
        @Body feedRequestDto: FeedRequestDto,
    )

    @PUT("feeds/{feedId}")
    suspend fun putFeed(
        @Path("feedId") feedId: Long,
        @Body feedRequestDto: FeedRequestDto,
    )

    @GET("feeds/{feedId}")
    suspend fun getFeed(
        @Path("feedId") feedId: Long,
    ): FeedDetailResponseDto

    @GET("feeds/popular")
    suspend fun getPopularFeeds(): PopularFeedsResponseDto

    @GET("feeds/interest")
    suspend fun getUserInterestFeeds(): UserInterestFeedsResponseDto

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

    @GET("feeds/{feedId}/comments")
    suspend fun getComments(
        @Path("feedId") feedId: Long,
    ): CommentsResponseDto

    @POST("feeds/{feedId}/comments")
    suspend fun postComment(
        @Path("feedId") feedId: Long,
        @Body commentRequestDto: CommentRequestDto,
    )

    @PUT("feeds/{feedId}/comments/{commentId}")
    suspend fun putComment(
        @Path("feedId") feedId: Long,
        @Path("commentId") commentId: Long,
        @Body commentRequestDto: CommentRequestDto,
    )

    @DELETE("feeds/{feedId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("feedId") feedId: Long,
        @Path("commentId") commentId: Long,
    )

    @POST("feeds/{feedId}/comments/{commentId}/spoiler")
    suspend fun postSpoilerComment(
        @Path("feedId") feedId: Long,
        @Path("commentId") commentId: Long,
    )

    @POST("feeds/{feedId}/comments/{commentId}/impertinence")
    suspend fun postImpertinenceComment(
        @Path("feedId") feedId: Long,
        @Path("commentId") commentId: Long,
    )
}
