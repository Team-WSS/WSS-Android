package com.into.websoso.data.library.model

data class NovelEntity(
    val userNovelId: Long,
    val novelId: Long,
    val author: String,
    val title: String,
    val novelImage: String,
    val novelRating: Float,
)
