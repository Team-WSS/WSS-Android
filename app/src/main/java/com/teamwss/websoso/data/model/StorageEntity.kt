package com.teamwss.websoso.data.model

data class StorageEntity(
    val isLoadable: Boolean,
    val userNovelCount: Int,
    val userNovelRating: Double,
    val userNovels: List<UserNovel>,
) {
    data class UserNovel(
        val author: String,
        val novelId: Int,
        val novelImage: String,
        val novelRating: Double,
        val title: String,
        val userNovelId: Int,
    ){
        val isRatingVisible: Boolean
            get() = novelRating != 0.0
    }
}