package com.into.websoso.data.mapper

import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.data.model.NotificationEntity
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.remote.response.NotificationDetailResponseDto
import com.into.websoso.data.remote.response.NotificationsResponseDto

fun NotificationsResponseDto.toData(): NotificationsEntity =
    NotificationsEntity(
        isLoadable = isLoadable,
        notifications = notifications.map {
            NotificationEntity(
                notificationId = it.notificationId,
                notificationImage = it.notificationImage,
                notificationTitle = it.notificationTitle,
                notificationBody = it.notificationBody,
                createdDate = it.createdDate,
                isRead = it.isRead,
                isNotification = it.isNotification,
                feedId = it.feedId,
            )
        },
    )

fun NotificationDetailResponseDto.toData(): NotificationDetailEntity =
    NotificationDetailEntity(
        notificationTitle = notificationTitle,
        notificationCreatedDate = notificationCreatedDate,
        notificationDetail = notificationDetail,
    )
