package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingModel(
    val userNovelId: Long,
    val novelTitle: String,
    val userNovelRating: Float,
    val readStatus: String,
    val startDate: String?,
    val endDate: String?,
    var uiReadStatus: ReadStatus = ReadStatus.valueOf(readStatus),
    val ratingDateModel: RatingDateModel =
        RatingDateModel(
            currentStartDate = startDate.toFormattedDate(),
            currentEndDate = endDate.toFormattedDate(),
            previousStartDate = startDate.toFormattedDate(),
            previousEndDate = endDate.toFormattedDate(),
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
    var previousStartDate: Triple<Int, Int, Int>? = null,
    var previousEndDate: Triple<Int, Int, Int>? = null,
)

data class RatingKeywordModel(
    val categories: List<CategoryModel>,
    var previousSelectedKeywords: List<CategoryModel.KeywordModel> =
        categories.flatMap {
            it.keywords.filter {
                    keyword ->
                keyword.isSelected
            }
        },
    var currentSelectedKeywords: List<CategoryModel.KeywordModel> = emptyList(),
) {
    data class CategoryModel(
        val categoryName: String,
        val keywords: List<KeywordModel>,
    ) {
        data class KeywordModel(
            val keywordId: Long,
            val keywordName: String,
            val isSelected: Boolean = false,
        )
    }
}
