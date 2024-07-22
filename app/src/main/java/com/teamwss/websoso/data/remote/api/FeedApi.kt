package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.FeedsRequestDto
import com.teamwss.websoso.data.remote.response.FeedsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
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
}
