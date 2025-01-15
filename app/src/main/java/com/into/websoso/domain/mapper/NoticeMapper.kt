package com.into.websoso.domain.mapper

import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.model.NotificationsEntity.NotificationEntity
import com.into.websoso.domain.model.Notice
import com.into.websoso.domain.model.NoticeInfo
import com.into.websoso.domain.model.NoticeType
import com.into.websoso.domain.usecase.GetNoticeListUseCase.Companion.DEFAULT_INTRINSIC_ID

fun NotificationsEntity.toDomain(): NoticeInfo {
    return NoticeInfo(
        isLoadable = isLoadable,
        lastNoticeId = notifications.maxOf { it.notificationId },
        notices = notifications.map { it.toDomain() },
    )
}

fun NotificationEntity.toDomain(): Notice {
    return Notice(
        id = notificationId,
        noticeIconImage = notificationImage,
        noticeTitle = notificationTitle,
        noticeType = getNoticeType(),
        noticeDescription = notificationDescription,
        createdDate = createdDate,
        isRead = isRead,
        intrinsicId = getIntrinsicId(),
    )
}

private fun NotificationEntity.getNoticeType(): NoticeType {
    return NoticeType.from(
        when {
            isNotice -> "NOTICE"
            else -> "FEED"
        },
    )
}

private fun NotificationEntity.getIntrinsicId(): Long {
    return when {
        isNotice -> notificationId
        feedId != null -> feedId
        else -> DEFAULT_INTRINSIC_ID
    }
}
