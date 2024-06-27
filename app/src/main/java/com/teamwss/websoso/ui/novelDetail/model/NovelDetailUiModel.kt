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
    )

    data class NovelModel(
        val novelTitle: String,
        val novelImage: String,
        val novelGenres: List<String>,
        val novelGenreImage: String,
        val isNovelCompleted: Boolean,
        val author: String,
    ) {
        val formattedNovelGenres: String
            get() = novelGenres.joinToString(" ・ ")
    }

    data class UserRatingModel(
        val interestCount: Int,
        val novelRating: Float,
        val novelRatingCount: Int,
        val feedCount: Int,
    ) {
        val formattedNovelRating: String
            get() = "$novelRating ($novelRatingCount)"
    }
}
