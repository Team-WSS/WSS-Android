package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.NotificationPushEnabledRequestDto
import com.into.websoso.data.remote.response.NotificationPushEnabledResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PushMessageApi {
    @GET("users/push-settings")
    suspend fun getUserPushEnabled(): NotificationPushEnabledResponseDto

    @POST("users/push-settings")
    suspend fun postUserPushEnabled(
        @Body notificationPushEnabledRequestDto: NotificationPushEnabledRequestDto,
    )
}
