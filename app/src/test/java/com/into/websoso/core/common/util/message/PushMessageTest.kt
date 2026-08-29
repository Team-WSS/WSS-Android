package com.into.websoso.core.common.util.message

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushMessageTest {
    @Test
    fun `휴재 복귀 알림 페이로드는 작품 상세로 이동한다`() {
        val pushMessage = PushMessage.from(HIATUS_RETURN_PAYLOAD)

        assertEquals(PushDestination.NOVEL, pushMessage?.destination)
        assertEquals(12345L, pushMessage?.novelId)
        assertEquals(67890L, pushMessage?.notificationId)
    }

    @Test
    fun `값이 없는 필드가 빈 문자열로 와도 null로 파싱한다`() {
        val pushMessage = PushMessage.from(HIATUS_RETURN_PAYLOAD)

        assertNull(pushMessage?.feedId)
    }

    @Test
    fun `제목과 내용을 페이로드에서 그대로 사용한다`() {
        val pushMessage = PushMessage.from(HIATUS_RETURN_PAYLOAD)

        assertEquals("휴재 복귀 알림", pushMessage?.title)
        assertEquals("작품명 작품에 새로운 회차가 올라왔어요.", pushMessage?.body)
    }

    @Test
    fun `피드 알림은 작품 ID가 없으므로 피드 상세로 이동한다`() {
        val pushMessage = PushMessage.from(
            mapOf(
                "title" to "댓글 알림",
                "body" to "내 글에 댓글이 달렸어요.",
                "feedId" to "4508",
                "novelId" to "",
                "view" to "",
                "notificationId" to "3764",
            ),
        )

        assertEquals(PushDestination.FEED, pushMessage?.destination)
        assertEquals(4508L, pushMessage?.feedId)
    }

    @Test
    fun `피드와 작품 ID가 모두 없으면 알림 상세로 이동한다`() {
        val pushMessage = PushMessage.from(
            mapOf(
                "title" to "공지",
                "body" to "공지사항입니다.",
                "feedId" to "",
                "novelId" to "",
                "notificationId" to "3746",
            ),
        )

        assertEquals(PushDestination.NOTIFICATION_DETAIL, pushMessage?.destination)
    }

    @Test
    fun `알림 ID가 빈 문자열이면 예외 대신 null을 반환한다`() {
        val pushMessage = PushMessage.from(
            mapOf(
                "title" to "휴재 복귀 알림",
                "body" to "새로운 회차가 올라왔어요.",
                "novelId" to "12345",
                "notificationId" to "",
            ),
        )

        assertNull(pushMessage)
    }

    // 기본 문구는 문자열 리소스라 표시 시점에 채우고, 파싱 단계에서는 없음을 그대로 남긴다
    @Test
    fun `제목과 내용이 없으면 비워 둔다`() {
        val pushMessage = PushMessage.from(mapOf("notificationId" to "1"))

        assertNull(pushMessage?.title)
        assertNull(pushMessage?.body)
    }

    companion object {
        // 서버가 실제로 내려주는 휴재 복귀 알림 페이로드
        private val HIATUS_RETURN_PAYLOAD = mapOf(
            "title" to "휴재 복귀 알림",
            "body" to "작품명 작품에 새로운 회차가 올라왔어요.",
            "feedId" to "",
            "novelId" to "12345",
            "view" to "",
            "notificationId" to "67890",
        )
    }
}
