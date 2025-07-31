package com.into.websoso.domain.library.model

enum class ReadStatus(
    val key: String,
    val label: String,
) {
    WATCHING("WATCHING", "보는중"),
    WATCHED("WATCHED", "봤어요"),
    QUIT("QUIT", "하차함"),
    ;

    companion object {
        fun from(key: String): ReadStatus? = entries.find { it.key == key }
    }
}
