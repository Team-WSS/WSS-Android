package com.teamwss.websoso.data.model

data class StorageEntity(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovelRating: Float,
    val userNovels: List<LibraryNovelEntity>,
) {
    data class LibraryNovelEntity(
        val author: String,
        val novelId: Long,
        val novelImage: String,
        val novelRating: Float,
        val title: String,
        val userNovelId: Long,
    ){
        val isRatingVisible: Boolean
            get() = novelRating != 0f
    }
}