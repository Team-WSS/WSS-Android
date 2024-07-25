package com.teamwss.websoso.domain.model

data class NovelDetail(
    val userNovel: UserNovel,
    val novel: Novel,
    val userRating: UserRating,
) {
    data class UserNovel(
        val userNovelId: Long?,
        val readStatus: String?,
        val startDate: String?,
        val endDate: String?,
        val isUserNovelInterest: Boolean,
        val userNovelRating: Float,
        val hasUserNovelInfo: Boolean = userNovelId != null,
    )

    data class Novel(
        val novelTitle: String,
        val novelImage: String,
        val novelGenres: List<String>,
        val novelGenreImage: String,
        val isNovelCompletedText: String,
        val author: String,
    )

    data class UserRating(
        val interestCount: Int,
        val novelRating: Float,
        val novelRatingCount: Int,
        val feedCount: Int,
    )
}
