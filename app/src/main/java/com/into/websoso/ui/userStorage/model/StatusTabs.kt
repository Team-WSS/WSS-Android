package com.into.websoso.ui.userStorage.model

enum class StorageTab(
    val position: Int,
    val title: String,
    val readStatus: String,
) {
    INTEREST(0, "관심", "INTEREST"),
    WATCHING(1, "보는중", "WATCHING"),
    WATCHED(2, "봤어요", "WATCHED"),
    QUIT(3, "하차", "QUIT");

    companion object {
        fun fromPosition(position: Int): StorageTab {
            return entries.find { it.position == position }
                ?: throw IllegalArgumentException("Invalid position: $position")
        }
    }
}