package com.into.websoso.core.common.util.message

data class PushMessage(
    val title: String?,
    val body: String?,
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
        fun from(data: Map<String, String>): PushMessage? {
            val notificationId = data["notificationId"]?.toLongOrNull() ?: return null

            return PushMessage(
                title = data["title"],
                body = data["body"],
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
