package com.into.websoso.core.network.datasource.user

import com.into.websoso.core.network.datasource.user.model.UserFeedsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("users/{userId}/feeds")
    suspend fun getUserFeeds(
        @Path("userId") userId: Long,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): UserFeedsResponseDto
}
