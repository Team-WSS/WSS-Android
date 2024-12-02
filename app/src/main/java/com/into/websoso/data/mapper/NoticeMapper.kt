package com.into.websoso.data.mapper

import com.into.websoso.data.model.NoticesEntity
import com.into.websoso.data.model.NoticesEntity.NoticeEntity
import com.into.websoso.data.remote.response.NoticesResponseDto

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