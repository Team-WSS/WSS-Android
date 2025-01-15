package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NotificationsEntity
import com.into.websoso.data.remote.api.NoticeApi
import javax.inject.Inject

class NoticeRepository @Inject constructor(
    private val noticeApi: NoticeApi,
) {

    suspend fun fetchNotices(lastNotificationId: Long, size: Int): NotificationsEntity {
        return noticeApi.getNotices(lastNotificationId, size).toData()
    }
}
