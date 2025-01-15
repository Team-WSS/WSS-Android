package com.into.websoso.data.mapper

import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.model.NotificationsEntity.NotificationEntity
import com.into.websoso.data.remote.response.NotificationsResponseDto

fun NotificationsResponseDto.toData(): NotificationsEntity = NotificationsEntity(
    isLoadable = isLoadable,
    notifications = notifications.map {
        NotificationEntity(
            notificationId = it.notificationId,
            notificationImage = it.notificationImage,
            notificationTitle = it.notificationTitle,
            notificationDescription = it.notificationDescription,
            createdDate = it.createdDate,
            isRead = it.isRead,
            isNotice = it.isNotice,
            feedId = it.feedId,
        )
    }
)
