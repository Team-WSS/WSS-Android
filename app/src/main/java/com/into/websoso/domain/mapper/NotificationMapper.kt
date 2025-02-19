package com.into.websoso.domain.mapper

import com.into.websoso.data.model.NotificationEntity
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.domain.model.Notification
import com.into.websoso.domain.model.NotificationInfo

fun NotificationsEntity.toDomain(): NotificationInfo =
    NotificationInfo(
        isLoadable = isLoadable,
        lastNotificationId = notifications.last().notificationId,
        notifications = notifications.map { it.toDomain() },
    )

fun NotificationEntity.toDomain(): Notification =
    Notification(
        id = notificationId,
        notificationIconImage = notificationImage,
        notificationTitle = getLineChangeIgnoredTitle(),
        notificationType = getNotificationType(),
        notificationDescription = notificationBody,
        createdDate = createdDate,
        isRead = isRead,
        intrinsicId = getIntrinsicId(),
    )
