package com.into.websoso.ui.notification.model

import com.into.websoso.domain.model.NotificationType

data class NotificationInfoModel(
    val isLoadable: Boolean = true,
    val lastNotificationId: Long = 0,
    val notifications: List<NotificationModel> = emptyList(),
)

data class NotificationModel(
    val id: Long,
    val notificationIconImage: String,
    val notificationTitle: String,
    val notificationType: NotificationType,
    val notificationDescription: String,
    val createdDate: String,
    val isRead: Boolean,
    val intrinsicId: Long,
) {
    companion object {
        private val LINE_CHANGE_NORM = Regex("[\n\r]")

        fun String.getIgnoreLineChangeTitle(): String = this.replace(LINE_CHANGE_NORM, " ").trim()
    }
}
