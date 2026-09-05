package com.into.websoso.domain.model

enum class NotificationType {
    NOTICE,
    FEED,
    NOVEL,
    NONE,
    ;

    companion object {
        fun from(value: String): NotificationType =
            when (value) {
                "NOTICE" -> NOTICE
                "FEED" -> FEED
                "NOVEL" -> NOVEL
                else -> NONE
            }
    }
}
