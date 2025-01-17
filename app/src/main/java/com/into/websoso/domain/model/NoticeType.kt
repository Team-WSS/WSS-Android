package com.into.websoso.domain.model

enum class NoticeType {
    NOTICE,
    FEED,
    NONE,
    ;

    companion object {
        fun from(value: String): NoticeType =
            when (value) {
                "NOTICE" -> NOTICE
                "FEED" -> FEED
                else -> NONE
            }
    }
}
