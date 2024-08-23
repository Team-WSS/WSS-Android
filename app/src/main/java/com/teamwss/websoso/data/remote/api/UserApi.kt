package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import com.teamwss.websoso.data.remote.response.UserProfileStatusResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @GET("users/profile-status")
    suspend fun getProfileStatus(): UserProfileStatusResponseDto

    @PATCH("users/profile-status")
    suspend fun patchProfileStatus(
        @Body userProfileStatusRequestDto: UserProfileStatusRequestDto,
    )
}