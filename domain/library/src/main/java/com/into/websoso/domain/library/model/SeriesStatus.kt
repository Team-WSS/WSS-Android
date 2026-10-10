package com.into.websoso.domain.library.model

enum class SeriesStatus(
    val key: String,
    val label: String,
) {
    IN_SERIES("IN_SERIES", "연재중"),
    COMPLETED("COMPLETED", "완결작"),
    ;

    companion object {
        fun from(key: String): SeriesStatus? = entries.find { it.key == key }
    }
}
