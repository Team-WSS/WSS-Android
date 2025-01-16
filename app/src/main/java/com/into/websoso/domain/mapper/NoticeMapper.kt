package com.into.websoso.domain.mapper

import com.into.websoso.data.model.NotificationEntity
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.domain.model.Notice
import com.into.websoso.domain.model.NoticeInfo

fun NotificationsEntity.toDomain(): NoticeInfo =
    NoticeInfo(
        isLoadable = isLoadable,
        lastNoticeId = notifications.maxOf { it.notificationId },
        notices = notifications.map { it.toDomain() },
    )

fun NotificationEntity.toDomain(): Notice =
    Notice(
        id = notificationId,
        noticeIconImage = notificationImage,
        noticeTitle = notificationTitle,
        noticeType = getNoticeType(),
        noticeDescription = notificationDescription,
        createdDate = createdDate,
        isRead = isRead,
        intrinsicId = getIntrinsicId(),
    )
