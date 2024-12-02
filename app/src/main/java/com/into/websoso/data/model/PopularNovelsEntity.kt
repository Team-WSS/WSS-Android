package com.into.websoso.data.model

data class PopularNovelsEntity(
    val popularNovels: List<PopularNovelEntity>,
) {
    data class PopularNovelEntity(
        val avatarImage: String?,
        val feedContent: String,
        val nickname: String?,
        val novelId: Long,
        val novelImage: String,
        val title: String,
    )
}