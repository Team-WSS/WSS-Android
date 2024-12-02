package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NoticesEntity
import com.into.websoso.data.remote.api.NoticeApi
import javax.inject.Inject

class NoticeRepository @Inject constructor(
    private val noticeApi: NoticeApi,
) {

    suspend fun fetchNotices(): NoticesEntity {
        return noticeApi.getNotices().toData()
    }
}