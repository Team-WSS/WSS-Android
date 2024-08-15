package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import retrofit2.http.DELETE
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {

    @GET("users/email")
    suspend fun getUserEmail(): UserEmailResponseDto

    @GET("blocks")
    suspend fun getBlockedUser(): BlockedUsersResponseDto

    @DELETE("blocks/{blockId}")
    suspend fun deleteBlockedUser(
        @Path("blockId") blockId: Long,
    )

    @GET("users/user-novel-stats")
    suspend fun getUserNovelStats(): UserNovelStatsResponseDto
}