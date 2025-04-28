package com.into.websoso.ui.detailExplore.info.model

enum class SeriesStatus(
    val title: String,
    val isCompleted: Boolean,
) {
    IN_SERIES("연재중", isCompleted = false),
    COMPLETED("완결작", isCompleted = true),
    ;

    companion object {
        fun from(title: String): SeriesStatus = entries.find { it.title == title } ?: throw IllegalArgumentException()

        fun fromIsCompleted(isCompleted: Boolean): SeriesStatus =
            entries.find { it.isCompleted == isCompleted } ?: throw IllegalArgumentException(
                "Unknown isCompleted value: $isCompleted",
            )
    }
}
