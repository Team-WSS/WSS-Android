package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.NotificationsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NoticeApi {

    @GET("notifications")
    suspend fun getNotices(
        @Query("lastNotificationId") lastNotificationId: Long,
    ): NotificationsResponseDto
}
