package com.into.websoso.ui.mapper

import com.into.websoso.domain.model.Notification
import com.into.websoso.ui.notification.model.NotificationModel
import com.into.websoso.ui.notification.model.NotificationModel.Companion.getIgnoreLineChangeTitle

fun Notification.toUi(): NotificationModel =
    NotificationModel(
        id = notificationId,
        notificationIconImage = notificationIconImage,
        notificationTitle = notificationTitle.getIgnoreLineChangeTitle(),
        notificationType = getNotificationType(),
        notificationDescription = notificationDescription,
        createdDate = createdDate,
        isRead = isRead,
        intrinsicId = intrinsicId,
    )
