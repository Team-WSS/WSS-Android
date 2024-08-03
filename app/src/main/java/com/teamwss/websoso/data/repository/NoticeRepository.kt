package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NoticesEntity
import com.teamwss.websoso.data.remote.api.NoticeApi
import javax.inject.Inject

class NoticeRepository @Inject constructor(
    private val noticeApi: NoticeApi,
) {

    suspend fun fetchNotices(): NoticesEntity {
        return noticeApi.getNotices().toData()
    }

}