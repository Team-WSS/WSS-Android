package com.into.websoso.domain.model

enum class NovelNotificationType {
    COMPLETION,
    HIATUS_RETURN,
    ;

    companion object {
        fun from(value: String): NovelNotificationType =
            when (value) {
                HIATUS_RETURN.name -> HIATUS_RETURN
                else -> COMPLETION
            }
    }
}
