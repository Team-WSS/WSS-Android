package com.into.websoso.domain.library.model

enum class ReadStatus(val key:String) {
    WATCHING("WATCHING"),
    WATCHED("WATCHED"),
    QUIT("QUIT"),
    ;

    companion object {
        fun valueOf(name: String): ReadStatus =
            entries.find { readStatus -> readStatus.name == name }
                ?: throw IllegalArgumentException()
    }
}
