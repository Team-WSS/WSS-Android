package com.into.websoso.data.model

data class PopularNovelsEntity(
    val popularNovels: List<PopularNovelEntity>,
) {
    data class PopularNovelEntity(
        val author: String,
        val avatarImage: String?,
        val feedContent: String?,
        val genreName: String,
        val isNovelCompleted: Boolean,
        val keywords: List<String>,
        val nickname: String?,
        val novelDescription: String,
        val novelId: Long,
        val novelImage: String,
        val title: String,
    ) {
        val hasUserFeed: Boolean
            get() = !feedContent.isNullOrBlank() && !avatarImage.isNullOrBlank() && !nickname.isNullOrBlank()
    }
}
