package com.teamwss.websoso.ui.userStorage.model

data class UserStorageModel(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovelRating: Float,
    val userNovels: List<StorageNovelModel>,
) {
    data class StorageNovelModel(
        val author: String,
        val novelId: Long,
        val novelImage: String,
        val title: String,
        val userNovelId: Long,
        val novelRating: Float,
    ) {
        private val visibleNovelRating: Float? = novelRating.takeIf { it != 0.0f }
        val isRatingVisible: Boolean = visibleNovelRating != null
    }
}