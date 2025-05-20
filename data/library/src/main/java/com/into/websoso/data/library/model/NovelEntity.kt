package com.into.websoso.data.library.model

data class NovelEntity(
    val userNovelId: Long,
    val novelId: Long,
    val author: String,
    val title: String,
    val novelImage: String,
    val novelRating: Float,
)

data class UserStorageEntity(
    val isLoadable: Boolean,
    val userNovelCount: Long,
    val userNovelRating: Float,
    val userNovels: List<NovelEntity>,
)
