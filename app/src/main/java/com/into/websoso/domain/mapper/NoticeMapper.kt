package com.into.websoso.domain.mapper

import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.model.NotificationsEntity.NotificationEntity
import com.into.websoso.domain.model.Notice
import com.into.websoso.domain.model.NoticeInfo
import com.into.websoso.domain.model.NoticeType

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
        noticeType = NoticeType.from(
            when {
                isNotice -> "NOTICE"
                else -> "FEED"
            },
        ),
        noticeDescription = notificationDescription,
        createdDate = createdDate,
        isRead = isRead,
        feedId = feedId,
    )
}
