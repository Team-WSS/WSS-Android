package com.into.websoso.data.model

import com.into.websoso.domain.model.NoticeType
import com.into.websoso.domain.usecase.GetNoticeListUseCase.Companion.DEFAULT_INTRINSIC_ID

data class NotificationsEntity(
    val isLoadable: Boolean,
    val notifications: List<NotificationEntity>,
)

data class NotificationEntity(
    val notificationId: Long,
    val notificationImage: String,
    val notificationTitle: String,
    val notificationDescription: String,
    val createdDate: String,
    val isRead: Boolean,
    val isNotice: Boolean,
    val feedId: Long?,
) {
    fun getNoticeType(): NoticeType =
        NoticeType.from(
            when {
                isNotice -> "NOTICE"
                else -> "FEED"
            },
        )

    fun getIntrinsicId(): Long =
        when {
            isNotice -> notificationId
            feedId != null -> feedId
            else -> DEFAULT_INTRINSIC_ID
        }
}
