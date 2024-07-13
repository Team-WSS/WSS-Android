package com.teamwss.websoso.data.model

data class NovelInfoEntity(
    val novelDescription: String,
    val platforms: List<PlatformEntity>,
    val attractivePoints: List<String>,
    val keywords: List<KeywordEntity>,
    val reviewCount: ReviewCountEntity,
) {
    data class PlatformEntity(
        val platformName: String,
        val platformImage: String,
        val platformUrl: String,
    )

    data class KeywordEntity(
        val keywordName: String,
        val keywordCount: Int,
    )

    data class ReviewCountEntity(
        val watchingCount: Int,
        val watchedCount: Int,
        val quitCount: Int,
    )
}
