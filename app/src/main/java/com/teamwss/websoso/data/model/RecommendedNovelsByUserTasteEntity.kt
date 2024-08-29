package com.teamwss.websoso.data.model

data class RecommendedNovelsByUserTasteEntity(
    val tasteNovels: List<RecommendedNovelByUserTasteEntity>,
) {
    data class RecommendedNovelByUserTasteEntity(
        val novelId: Long,
        val title: String,
        val author: String,
        val novelImage: String,
        val interestCount: Int,
        val novelRating: Double,
        val novelRatingCount: Int,
    )
}