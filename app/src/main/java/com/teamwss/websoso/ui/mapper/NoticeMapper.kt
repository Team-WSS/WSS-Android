package com.teamwss.websoso.ui.mapper


import com.teamwss.websoso.ui.notice.model.Notice

fun NoticeEntity.toUiModel(): Notice {
    return Notice(
        createDate = this.createDate,
        noticeTitle = this.noticeTitle,
        noticeContent = this.noticeContent,
    )
}

fun List<NoticeEntity>.toUiModel(): List<Notice> {
    return this.map { it.toUiModel() }
}