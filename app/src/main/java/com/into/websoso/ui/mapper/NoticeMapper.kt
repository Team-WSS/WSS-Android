package com.into.websoso.ui.mapper


import com.into.websoso.data.model.NotificationsEntity.NotificationEntity
import com.into.websoso.ui.notice.model.NoticeModel

fun NotificationEntity.toUi(): NoticeModel {
    return NoticeModel(
        createDate = this.createDate,
        noticeTitle = this.noticeTitle,
        noticeContent = this.noticeContent,
    )
}

fun List<NotificationEntity>.toUi(): List<NoticeModel> {
    return this.map { it.toUi() }
}
