package com.into.websoso.core.network.datasource.feed

import com.into.websoso.core.network.datasource.feed.model.request.CommentRequestDto
import com.into.websoso.core.network.datasource.feed.model.response.CommentsResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedDetailResponseDto
import com.into.websoso.core.network.datasource.feed.model.response.FeedsResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

interface FeedApi {
    @GET("feeds")
    suspend fun getFeeds(
        @Query("category") category: String?,
        @Query("feedsOption") feedsOption: String, // [New] 옵션 추가됨
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): FeedsResponseDto

    @GET("feeds/{feedId}")
    suspend fun getFeed(
        @Path("feedId") feedId: Long,
    ): FeedDetailResponseDto

    @Multipart
    @POST("feeds")
    suspend fun postFeed(
        @Part feedRequestDto: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?,
    )

    @Multipart
    @PUT("feeds/{feedId}")
    suspend fun putFeed(
        @Path("feedId") feedId: Long,
        @Part feedRequestDto: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?,
    )

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

@Module
@InstallIn(SingletonComponent::class)
internal object FeedApiModule {
    @Provides
    @Singleton
    internal fun provideFeedApi(retrofit: Retrofit): FeedApi = retrofit.create(FeedApi::class.java)
}
