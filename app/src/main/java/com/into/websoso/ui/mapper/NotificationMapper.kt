package com.into.websoso.ui.mapper

import com.into.websoso.domain.model.Notification
import com.into.websoso.domain.model.NotificationInfo
import com.into.websoso.ui.notification.model.NotificationInfoModel
import com.into.websoso.ui.notification.model.NotificationUiModel
import com.into.websoso.ui.notification.model.NotificationUiModel.Companion.getIgnoreLineChangeTitle

fun NotificationInfo.toUi(): NotificationInfoModel =
    NotificationInfoModel(
        isLoadable = isLoadable,
        lastNotificationId = lastNotificationId,
        notifications = notifications.map { it.toUi() },
    )

private fun Notification.toUi(): NotificationUiModel =
    NotificationUiModel(
        id = notificationId,
        notificationIconImage = notificationIconImage,
        notificationTitle = notificationTitle.getIgnoreLineChangeTitle(),
        notificationType = getNotificationType(),
        notificationDescription = notificationDescription,
        createdDate = createdDate,
        isRead = isRead,
        intrinsicId = intrinsicId,
    )
