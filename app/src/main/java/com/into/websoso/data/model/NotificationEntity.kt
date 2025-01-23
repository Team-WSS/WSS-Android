package com.into.websoso.data.model

import com.into.websoso.domain.model.NotificationType
import com.into.websoso.domain.usecase.GetNotificationListUseCase

data class NotificationEntity(
    val notificationId: Long,
    val notificationImage: String,
    val notificationTitle: String,
    val notificationBody: String,
    val createdDate: String,
    val isRead: Boolean,
    val isNotification: Boolean,
    val feedId: Long?,
) {
    fun getNotificationType(): NotificationType =
        NotificationType.from(
            when {
                isNotification -> "NOTICE"
                else -> "FEED"
            },
        )

    fun getIntrinsicId(): Long =
        when {
            isNotification -> notificationId
            feedId != null -> feedId
            else -> GetNotificationListUseCase.DEFAULT_INTRINSIC_ID
        }
}
