package com.into.websoso.domain.model

enum class NoticeType {
    NOTICE,
    FEED,
    NONE,
    ;

    companion object {
        fun from(value: String): NoticeType {
            return when (value) {
                "NOTICE" -> NOTICE
                "FEED" -> FEED
                else -> NONE
            }
        }
    }
}
