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
    val isNotice: Boolean,
    val feedId: Long?,
) {
    fun getIgnoreLineChangeTitle(): String = notificationTitle.replace(Regex(LINE_CHANGE), " ").trim()

    fun getNotificationType(): NotificationType =
        NotificationType.from(
            when {
                isNotice -> "NOTICE"
                else -> "FEED"
            },
        )

    fun getIntrinsicId(): Long =
        when {
            isNotice -> notificationId
            feedId != null -> feedId
            else -> GetNotificationListUseCase.DEFAULT_INTRINSIC_ID
        }

    companion object {
        private const val LINE_CHANGE = "[\n\r]"
    }
}
