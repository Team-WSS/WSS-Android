package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.NoticeEntity
import com.teamwss.websoso.data.model.NoticesEntity
import com.teamwss.websoso.data.remote.response.NoticesResponseDto

fun NoticesResponseDto.toData(): NoticesEntity {
    return NoticesEntity(
        notices = notices.map {
            NoticeEntity(
                createDate = it.createdDate,
                noticeTitle = it.noticeTitle,
                noticeContent = it.noticeContent,
            )
        }
    )
}