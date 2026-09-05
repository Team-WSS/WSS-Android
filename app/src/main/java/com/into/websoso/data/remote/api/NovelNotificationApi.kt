package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.request.NovelNotificationSettingRequestDto
import com.into.websoso.data.remote.request.NovelNotificationSubscriptionsDeleteRequestDto
import com.into.websoso.data.remote.response.NovelNotificationSettingResponseDto
import com.into.websoso.data.remote.response.NovelNotificationSubscriptionsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NovelNotificationApi {
    @GET("novels/{novelId}/notification")
    suspend fun getNovelNotificationSetting(
        @Path("novelId") novelId: Long,
    ): NovelNotificationSettingResponseDto

    @PUT("novels/{novelId}/notification")
    suspend fun putNovelNotificationSetting(
        @Path("novelId") novelId: Long,
        @Body novelNotificationSettingRequestDto: NovelNotificationSettingRequestDto,
    )

    @GET("users/me/notification/novels")
    suspend fun getNovelNotificationSubscriptions(
        @Query("notificationType") notificationType: String,
        @Query("lastSubscriptionId") lastSubscriptionId: Long,
        @Query("size") size: Int,
    ): NovelNotificationSubscriptionsResponseDto

    @HTTP(method = "DELETE", path = "users/me/notification/novels", hasBody = true)
    suspend fun deleteNovelNotificationSubscriptions(
        @Body novelNotificationSubscriptionsDeleteRequestDto: NovelNotificationSubscriptionsDeleteRequestDto,
    )
}
