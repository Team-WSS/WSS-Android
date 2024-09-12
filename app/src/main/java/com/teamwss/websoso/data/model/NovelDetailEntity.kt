package com.teamwss.websoso.data.model

data class NovelDetailEntity(
    val userNovel: UserNovelEntity,
    val novel: NovelEntity,
    val userRating: UserRatingEntity,
) {
    data class UserNovelEntity(
        val userNovelId: Long?,
        val readStatus: String?,
        val startDate: String?,
        val endDate: String?,
        val isUserNovelInterest: Boolean,
        val userNovelRating: Float,
    )

    data class NovelEntity(
        val novelTitle: String,
        val novelImage: String,
        val novelGenres: String,
        val novelGenreImage: String,
        val isNovelCompleted: Boolean,
        val author: String,
    )

    data class UserRatingEntity(
        val interestCount: Int,
        val novelRating: Float,
        val novelRatingCount: Int,
        val feedCount: Int,
    )
}
