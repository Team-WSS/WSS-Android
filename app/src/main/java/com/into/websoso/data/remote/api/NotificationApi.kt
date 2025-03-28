package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.NotificationDetailResponseDto
import com.into.websoso.data.remote.response.NotificationUnreadResponseDto
import com.into.websoso.data.remote.response.NotificationsResponseDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("lastNotificationId") lastNotificationId: Long,
        @Query("size") size: Int,
    ): NotificationsResponseDto

    @GET("notifications/{notificationId}")
    suspend fun getNotificationDetail(
        @Path("notificationId") notificationId: Long,
    ): NotificationDetailResponseDto

    @GET("notifications/unread")
    suspend fun getNotificationUnread(): NotificationUnreadResponseDto

    @POST("notifications/{notificationId}/read")
    suspend fun readNotification(
        @Path("notificationId") notificationId: Long,
    )
}
