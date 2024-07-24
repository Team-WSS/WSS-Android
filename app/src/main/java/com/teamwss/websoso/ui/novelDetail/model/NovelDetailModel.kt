package com.teamwss.websoso.ui.novelDetail.model

data class NovelDetailModel(
    val userNovel: UserNovelModel,
    val novel: NovelModel,
    val userRating: UserRatingModel,
) {
    data class UserNovelModel(
        val userNovelId: Long?,
        val readStatus: String?,
        val startDate: String?,
        val endDate: String?,
        val isUserNovelInterest: Boolean,
        val userNovelRating: Float,
        val hasUserNovelInfo: Boolean = userNovelId != null,
    )

    data class NovelModel(
        val novelTitle: String,
        val novelImage: String,
        val novelGenres: List<String>,
        val formattedNovelGenres: String = novelGenres.joinToString("/"),
        val novelGenreImage: String,
        val isNovelCompletedText: String,
        val author: String,
    )

    data class UserRatingModel(
        val interestCount: Int,
        val novelRating: Float,
        val novelRatingCount: Int,
        val formattedNovelRating: String = "$novelRating ($novelRatingCount)",
        val feedCount: Int,
    )

    val formattedNovelDetailSummary: String = "${novel.formattedNovelGenres} ・ ${novel.isNovelCompletedText} ・ ${novel.author}"
}
