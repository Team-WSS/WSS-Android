package com.into.websoso.domain.model

import com.into.websoso.data.model.NotificationEntity
import com.into.websoso.data.repository.NotificationRepository.Companion.DEFAULT_INTRINSIC_ID
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationTypeTest {
    @Test
    fun `공지 알림은 NOTICE 타입이고 공지 식별자로 알림 ID를 사용한다`() {
        val entity = notificationEntity(isNotice = true, feedId = null, novelId = null)

        assertEquals(NotificationType.NOTICE, entity.toNotification().getNotificationType())
        assertEquals(NOTIFICATION_ID, entity.getIntrinsicId())
    }

    @Test
    fun `피드 알림은 FEED 타입이고 식별자로 피드 ID를 사용한다`() {
        val entity = notificationEntity(isNotice = false, feedId = FEED_ID, novelId = null)

        assertEquals(NotificationType.FEED, entity.toNotification().getNotificationType())
        assertEquals(FEED_ID, entity.getIntrinsicId())
    }

    @Test
    fun `작품 알림은 NOVEL 타입이고 식별자로 작품 ID를 사용한다`() {
        val entity = notificationEntity(isNotice = false, feedId = null, novelId = NOVEL_ID)

        assertEquals(NotificationType.NOVEL, entity.toNotification().getNotificationType())
        assertEquals(NOVEL_ID, entity.getIntrinsicId())
    }

    @Test
    fun `공지와 작품 ID가 함께 오면 기존 동작대로 공지를 우선한다`() {
        val entity = notificationEntity(isNotice = true, feedId = null, novelId = NOVEL_ID)

        assertEquals(NotificationType.NOTICE, entity.toNotification().getNotificationType())
        assertEquals(NOTIFICATION_ID, entity.getIntrinsicId())
    }

    @Test
    fun `피드와 작품 ID가 함께 오면 기존 동작대로 피드를 우선한다`() {
        val entity = notificationEntity(isNotice = false, feedId = FEED_ID, novelId = NOVEL_ID)

        assertEquals(NotificationType.FEED, entity.toNotification().getNotificationType())
        assertEquals(FEED_ID, entity.getIntrinsicId())
    }

    @Test
    fun `식별자가 없는 알림은 NONE 타입이고 기본 식별자를 사용한다`() {
        val entity = notificationEntity(isNotice = false, feedId = null, novelId = null)

        assertEquals(NotificationType.NONE, entity.toNotification().getNotificationType())
        assertEquals(DEFAULT_INTRINSIC_ID, entity.getIntrinsicId())
    }

    private fun notificationEntity(
        isNotice: Boolean,
        feedId: Long?,
        novelId: Long?,
    ): NotificationEntity =
        NotificationEntity(
            notificationId = NOTIFICATION_ID,
            notificationImage = "",
            notificationTitle = "",
            notificationBody = "",
            createdDate = "",
            isRead = false,
            isNotice = isNotice,
            feedId = feedId,
            novelId = novelId,
        )

    private fun NotificationEntity.toNotification(): Notification =
        Notification(
            notificationId = notificationId,
            notificationIconImage = notificationImage,
            notificationTitle = notificationTitle,
            notificationDescription = notificationBody,
            createdDate = createdDate,
            isRead = isRead,
            isNotice = isNotice,
            feedId = feedId,
            novelId = novelId,
            intrinsicId = getIntrinsicId(),
        )

    companion object {
        private const val NOTIFICATION_ID = 100L
        private const val FEED_ID = 200L
        private const val NOVEL_ID = 300L
    }
}
