package com.into.websoso.ui.mapper


import com.into.websoso.data.model.NoticesEntity.NoticeEntity
import com.into.websoso.ui.notice.model.NoticeModel

fun NoticeEntity.toUi(): NoticeModel {
    return NoticeModel(
        createDate = this.createDate,
        noticeTitle = this.noticeTitle,
        noticeContent = this.noticeContent,
    )
}

fun List<NoticeEntity>.toUi(): List<NoticeModel> {
    return this.map { it.toUi() }
}