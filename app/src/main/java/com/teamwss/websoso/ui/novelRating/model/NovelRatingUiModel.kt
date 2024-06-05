package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingModel(
    val userNovelId: Long,
    val novelTitle: String,
    val userNovelRating: Float,
    val readStatus: String,
    val startDate: String?,
    val endDate: String?,
    var uiReadStatus: ReadStatus = ReadStatus.valueOf(readStatus),
    val ratingDateModel: RatingDateModel = RatingDateModel(
        currentStartDate = startDate.toFormattedDate(),
        currentEndDate = endDate.toFormattedDate(),
        pastStartDate = startDate.toFormattedDate(),
        pastEndDate = endDate.toFormattedDate()
    ),
) {
    companion object {
        fun String?.toFormattedDate(): Triple<Int, Int, Int>? {
            val date = this?.split("-") ?: return null
            return Triple(date[0].toInt(), date[1].toInt(), date[2].toInt())
        }
    }
}

data class RatingDateModel(
    var currentStartDate: Triple<Int, Int, Int>? = null,
    var currentEndDate: Triple<Int, Int, Int>? = null,
    var pastStartDate: Triple<Int, Int, Int>? = null,
    var pastEndDate: Triple<Int, Int, Int>? = null,
)

data class KeywordModel(
    val categories: List<Category>,
    var pastSelectedKeywords: List<Category.Keyword> = categories.flatMap { it.keywords.filter { keyword -> keyword.isSelected } },
    var currentSelectedKeywords: List<Category.Keyword> = emptyList(),
) {
    data class Category(
        val categoryName: String,
        val keywords: List<Keyword>,
    ) {
        data class Keyword(
            val keywordId: Long,
            val keywordName: String,
            val isSelected: Boolean = false,
        )
    }
}
