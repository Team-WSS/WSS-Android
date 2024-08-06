package com.teamwss.websoso.ui.mapper


import com.teamwss.websoso.data.model.NoticesEntity.NoticeEntity
import com.teamwss.websoso.ui.notice.model.NoticeModel

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