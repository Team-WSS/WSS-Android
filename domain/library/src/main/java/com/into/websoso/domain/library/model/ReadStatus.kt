package com.into.websoso.domain.library.model

enum class ReadStatus(
    val key: String,
) {
    WATCHING("WATCHING"),
    WATCHED("WATCHED"),
    QUIT("QUIT"),
    ;

    companion object {
        fun from(key: String): ReadStatus =
            entries.find { readStatus -> readStatus.key == key }
                ?: throw IllegalArgumentException()
    }
}
