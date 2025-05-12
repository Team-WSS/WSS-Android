package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.FCMTokenRequestDto
import com.into.websoso.data.remote.request.LogoutRequestDto
import com.into.websoso.data.remote.request.UserProfileRequestDto
import com.into.websoso.data.remote.request.WithdrawRequestDto
import com.into.websoso.data.remote.response.UserNicknameValidityResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @GET("users/nickname/check")
    suspend fun getNicknameValidity(
        @Header("Authorization") authorization: String,
        @Query("nickname") nickname: String,
    ): UserNicknameValidityResponseDto

    @POST("users/profile")
    suspend fun postUserProfile(
        @Header("Authorization") authorization: String,
        @Body userProfileRequestDto: UserProfileRequestDto,
    )

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String,
        @Body loginResponseDto: LogoutRequestDto,
    )

    @POST("auth/withdraw")
    suspend fun withdraw(
        @Header("Authorization") authorization: String,
        @Body withdrawRequestDto: WithdrawRequestDto,
    )

    @POST("users/fcm-token")
    suspend fun postFCMToken(
        @Header("Authorization") authorization: String,
        @Body fcmTokenRequestDto: FCMTokenRequestDto,
    )
}
