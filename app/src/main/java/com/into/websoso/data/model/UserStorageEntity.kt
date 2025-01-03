package com.into.websoso.data.model

data class UserStorageEntity(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovelRating: Float,
    val userNovels: List<StorageNovelEntity>,
) {
    data class StorageNovelEntity(
        val author: String,
        val novelId: Long,
        val novelImage: String,
        val novelRating: Float,
        val title: String,
        val userNovelId: Long,
    )
}