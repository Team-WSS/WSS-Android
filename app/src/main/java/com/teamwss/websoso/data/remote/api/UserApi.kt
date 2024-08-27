package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.UserInfoRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.UserInfoResponseDto
import com.teamwss.websoso.data.remote.response.UserNicknameValidityResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import com.teamwss.websoso.data.remote.response.UserProfileStatusResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/info")
    suspend fun getUserInfo(): UserInfoResponseDto

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

    @PUT("users/info")
    suspend fun putUserInfo(
        @Body userInfoRequestDto: UserInfoRequestDto,
    )

    @GET("users/nickname/check")
    suspend fun getNicknameValidity(
        @Query("nickname") nickname: String,
    ): UserNicknameValidityResponseDto

    @PATCH("users/my-profile")
    suspend fun patchProfile(
        @Body userProfileRequestDto: UserProfileRequestDto,
    )
}