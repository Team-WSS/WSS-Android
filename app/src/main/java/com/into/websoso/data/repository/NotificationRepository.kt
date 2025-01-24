package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.remote.api.NotificationApi
import com.into.websoso.data.remote.request.PushSettingRequestDto
import javax.inject.Inject

class NotificationRepository
@Inject constructor(
    private val notificationApi: NotificationApi,
) {
    suspend fun fetchNotifications(
        lastNotificationId: Long,
        size: Int,
    ): NotificationsEntity = notificationApi.getNotifications(lastNotificationId, size).toData()

    suspend fun fetchNotificationDetail(notificationId: Long): NotificationDetailEntity =
        notificationApi.getNotificationDetail(notificationId).toData()

    suspend fun fetchNotificationUnread(): Boolean =
        notificationApi.getNotificationUnread().hasUnreadNotifications

    suspend fun fetchPushSetting(): Boolean = notificationApi.getUserPushSettings().isPushEnabled

    suspend fun savePushSetting(isEnabled: Boolean) {
        notificationApi.postUserPushSettings(
            pushSettingRequestDto = PushSettingRequestDto(
                isPushEnabled = isEnabled,
            ),
        )
    }
}
