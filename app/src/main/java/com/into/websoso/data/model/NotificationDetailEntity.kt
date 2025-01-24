package com.into.websoso.data.model

import java.time.LocalDate

data class NotificationDetailEntity(
    val notificationTitle: String,
    val notificationCreatedDate: String,
    val notificationDetail: String,
) {
    companion object {
        val DEFAULT = NotificationDetailEntity(
            notificationTitle = "웹소소 공지입니다",
            notificationCreatedDate = LocalDate.now().toString(),
            notificationDetail = "안녕하세요, 웹소소입니다.\n" +
                "\n" +
                "웹소설을 애정하는 팀원들이 모여,\n" +
                "‘웹소설에 푹 빠지는 곳, 웹소소’를 만들었습니다.\n" +
                "\n" +
                "웹소소가 여러분의 소소한 즐거움이 되면 좋겠습니다.\n" +
                "함께 해주셔서 감사합니다.\n" +
                "\n" +
                "웹소소 드림 www.websoso.kr",
        )
    }
}
