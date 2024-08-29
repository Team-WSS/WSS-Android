package com.teamwss.websoso.ui.detailExplore.info.model

enum class SeriesStatus(val title: String, val isCompleted: Boolean) {
    IN_SERIES("연재중", isCompleted = false),
    COMPLETED("완결작", isCompleted = true);

    companion object {

        fun from(title: String): SeriesStatus {
            return entries.find { it.title == title } ?: throw IllegalArgumentException()
        }
    }
}