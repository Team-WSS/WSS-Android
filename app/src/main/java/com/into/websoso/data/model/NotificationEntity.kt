package com.into.websoso.data.model

import com.into.websoso.data.repository.NotificationRepository.Companion.DEFAULT_INTRINSIC_ID

data class NotificationEntity(
    val notificationId: Long,
    val notificationImage: String,
    val notificationTitle: String,
    val notificationBody: String,
    val createdDate: String,
    val isRead: Boolean,
    val isNotice: Boolean,
    val feedId: Long?,
    val novelId: Long?,
) {
    fun getIntrinsicId(): Long =
        when {
            isNotice -> notificationId
            feedId != null -> feedId
            novelId != null -> novelId
            else -> DEFAULT_INTRINSIC_ID
        }
}
