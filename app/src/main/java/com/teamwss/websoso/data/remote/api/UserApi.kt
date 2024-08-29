package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.UserInfoRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileStatusRequestDto
import com.teamwss.websoso.data.remote.response.BlockedUsersResponseDto
import com.teamwss.websoso.data.remote.response.GenrePreferenceResponseDto
import com.teamwss.websoso.data.remote.response.MyProfileResponseDto
import com.teamwss.websoso.data.remote.response.NovelPreferenceResponseDto
import com.teamwss.websoso.data.remote.response.UserInfoResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import com.teamwss.websoso.data.remote.response.UserProfileStatusResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @GET("users/my-profile")
    suspend fun getMyProfile(): MyProfileResponseDto

    @GET("users/{userId}/preferences/genres")
    suspend fun getGenrePreference(
        @Path("userId") userId: Long,
    ): GenrePreferenceResponseDto

    @GET("users/{userId}/preferences/attractive-points")
    suspend fun getNovelPreferences(
        @Path("userId") userId: Long,
    ): NovelPreferenceResponseDto

    @POST("users/profile")
    suspend fun postUserProfile(
        @Body userProfileRequestDto: UserProfileRequestDto,
    )
    @GET("users/profile/{userId}")
    suspend fun getOtherUserProfile(
        @Path("userId") userId: Long,
    ): OtherUserProfileResponseDto

}