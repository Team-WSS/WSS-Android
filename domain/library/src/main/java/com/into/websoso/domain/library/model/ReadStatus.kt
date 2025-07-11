package com.into.websoso.domain.library.model

enum class ReadStatus {
    WATCHING,
    WATCHED,
    QUIT,
    ;

    companion object {
        fun valueOf(name: String): ReadStatus =
            entries.find { readStatus -> readStatus.name == name }
                ?: throw IllegalArgumentException()
    }
}
