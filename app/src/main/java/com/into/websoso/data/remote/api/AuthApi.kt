package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.FCMTokenRequestDto
import com.into.websoso.data.remote.request.UserProfileRequestDto
import com.into.websoso.data.remote.response.UserNicknameValidityResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @GET("users/nickname/check")
    suspend fun getNicknameValidity(
        @Query("nickname") nickname: String,
    ): UserNicknameValidityResponseDto

    @POST("users/profile")
    suspend fun postUserProfile(
        @Body userProfileRequestDto: UserProfileRequestDto,
    )

    @POST("users/fcm-token")
    suspend fun postFCMToken(
        @Body fcmTokenRequestDto: FCMTokenRequestDto,
    )
}
