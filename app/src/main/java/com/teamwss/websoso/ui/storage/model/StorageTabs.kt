package com.teamwss.websoso.ui.storage.model

enum class StorageTab(val position: Int, val title: String) {
    INTEREST(0, "관심"),
    WATCHING(1, "보는중"),
    WATCHED(2, "봤어요"),
    QUITTING(3, "하차");

    companion object {
        fun fromPosition(position: Int): StorageTab {
            return values().find { it.position == position }
                ?: throw IllegalArgumentException("Invalid position: $position")
        }
    }
}