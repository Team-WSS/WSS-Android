package com.into.websoso.core.common.util.message

data class PushMessage(
    val title: String,
    val body: String,
    val feedId: Long?,
    val novelId: Long?,
    val notificationId: Long,
) {
    val destination: PushDestination
        get() = when {
            feedId != null -> PushDestination.FEED
            novelId != null -> PushDestination.NOVEL
            else -> PushDestination.NOTIFICATION_DETAIL
        }

    companion object {
        const val DEFAULT_TITLE = "웹소소"
        const val DEFAULT_BODY = "푸시 알림 메시지입니다"

        fun from(data: Map<String, String>): PushMessage? {
            val notificationId = data["notificationId"]?.toLongOrNull() ?: return null

            return PushMessage(
                title = data["title"] ?: DEFAULT_TITLE,
                body = data["body"] ?: DEFAULT_BODY,
                feedId = data["feedId"]?.toLongOrNull(),
                novelId = data["novelId"]?.toLongOrNull(),
                notificationId = notificationId,
            )
        }
    }
}

enum class PushDestination {
    FEED,
    NOVEL,
    NOTIFICATION_DETAIL,
}
