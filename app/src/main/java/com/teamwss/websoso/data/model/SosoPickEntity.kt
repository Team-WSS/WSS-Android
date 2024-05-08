package com.teamwss.websoso.data.model

data class SosoPickEntity(
    val novels: List<NovelEntity>
) {
    data class NovelEntity(
        val novelId: Long,
        val novelTitle: String,
        val novelCover: String,
    )
}