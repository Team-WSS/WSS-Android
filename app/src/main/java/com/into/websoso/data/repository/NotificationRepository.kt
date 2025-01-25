package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.remote.api.NotificationApi
import com.into.websoso.data.remote.request.NotificationPushEnabledRequestDto
import javax.inject.Inject

class NotificationRepository
    @Inject
    constructor(
        private val notificationApi: NotificationApi,
    ) {
        suspend fun fetchNotifications(
            lastNotificationId: Long,
            size: Int,
        ): NotificationsEntity = notificationApi.getNotifications(lastNotificationId, size).toData()

        suspend fun fetchNotificationDetail(notificationId: Long): NotificationDetailEntity =
            notificationApi.getNotificationDetail(notificationId).toData()

        suspend fun fetchUserPushEnabled(): Boolean = notificationApi.getUserPushEnabled().isPushEnabled

        suspend fun saveUserPushEnabled(isEnabled: Boolean) {
            notificationApi.postUserPushEnabled(
                notificationPushEnabledRequestDto = NotificationPushEnabledRequestDto(
                    isPushEnabled = isEnabled,
                ),
            )
        }

        suspend fun fetchNotificationUnread(): Boolean = notificationApi.getNotificationUnread().hasUnreadNotifications

        suspend fun fetchNotificationRead(notificationId: Long) {
            notificationApi.readNotification(notificationId)
        }
    }
