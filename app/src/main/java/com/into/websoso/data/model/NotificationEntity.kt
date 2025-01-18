package com.into.websoso.data.model

import com.into.websoso.domain.model.NoticeType
import com.into.websoso.domain.usecase.GetNoticeListUseCase

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
            else -> GetNoticeListUseCase.DEFAULT_INTRINSIC_ID
        }
}
