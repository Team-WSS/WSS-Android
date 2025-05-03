package com.into.websoso.ui.novelRating.model

enum class ReadStatus {
    WATCHING,
    WATCHED,
    QUIT,
    NONE,
    ;

    companion object {
        fun fromString(value: String?): ReadStatus =
            when (value) {
                "WATCHING" -> WATCHING
                "WATCHED" -> WATCHED
                "QUIT" -> QUIT
                else -> NONE
            }
    }
}
