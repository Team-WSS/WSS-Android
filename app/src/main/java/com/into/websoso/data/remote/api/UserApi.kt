package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.UserInfoRequestDto
import com.into.websoso.data.remote.request.UserProfileEditRequestDto
import com.into.websoso.data.remote.request.UserProfileStatusRequestDto
import com.into.websoso.data.remote.response.BlockedUsersResponseDto
import com.into.websoso.data.remote.response.GenrePreferenceResponseDto
import com.into.websoso.data.remote.response.MyProfileResponseDto
import com.into.websoso.data.remote.response.NovelPreferenceResponseDto
import com.into.websoso.data.remote.response.OtherUserProfileResponseDto
import com.into.websoso.data.remote.response.UserFeedsResponseDto
import com.into.websoso.data.remote.response.UserInfoDetailResponseDto
import com.into.websoso.data.remote.response.UserInfoResponseDto
import com.into.websoso.data.remote.response.UserNicknameValidityResponseDto
import com.into.websoso.data.remote.response.UserNovelStatsResponseDto
import com.into.websoso.data.remote.response.UserProfileStatusResponseDto
import com.into.websoso.data.remote.response.UserStorageResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/me")
    suspend fun getUserInfo(): UserInfoResponseDto

    @GET("users/info")
    suspend fun getUserInfoDetail(): UserInfoDetailResponseDto

    @GET("blocks")
    suspend fun getBlockedUser(): BlockedUsersResponseDto

    @DELETE("blocks/{blockId}")
    suspend fun deleteBlockedUser(
        @Path("blockId") blockId: Long,
    )

    @POST("blocks")
    suspend fun postBlockUser(
        @Query("userId") userId: Long,
    )

    @GET("users/{userId}/user-novel-stats")
    suspend fun getUserNovelStats(
        @Path("userId") userId: Long,
    ): UserNovelStatsResponseDto

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
        @Body userProfileEditRequestDto: UserProfileEditRequestDto,
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

    @GET("users/profile/{userId}")
    suspend fun getOtherUserProfile(
        @Path("userId") userId: Long,
    ): OtherUserProfileResponseDto

    @GET("users/{userId}/novels")
    suspend fun getUserStorage(
        @Path("userId") userId: Long,
        @Query("readStatus") readStatus: String,
        @Query("lastUserNovelId") lastUserNovelId: Long,
        @Query("size") size: Int,
        @Query("sortType") sortType: String,
    ): UserStorageResponseDto

    @GET("users/{userId}/feeds")
    suspend fun getUserFeeds(
        @Path("userId") userId: Long,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): UserFeedsResponseDto
}
