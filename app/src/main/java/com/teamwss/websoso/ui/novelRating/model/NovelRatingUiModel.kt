package com.teamwss.websoso.ui.novelRating.model

import com.teamwss.websoso.R
import java.util.Locale

data class NovelRatingModel(
    val novelTitle: String = "",
    val readStatus: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val userNovelRating: Float = 0f,
    val charmPoints: List<CharmPoint> = emptyList(),
    val userKeywords: List<NovelRatingKeywordModel> = emptyList(),
    val uiReadStatus: ReadStatus = ReadStatus.valueOf(readStatus ?: ReadStatus.WATCHING.name),
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

        fun String.toCharmPoint(): CharmPoint = CharmPoint.entries.find { it.value == this } ?: CharmPoint.WORLDVIEW
    }
}

data class RatingDateModel(
    val currentStartDate: Triple<Int, Int, Int>? = null,
    val currentEndDate: Triple<Int, Int, Int>? = null,
    val previousStartDate: Triple<Int, Int, Int>? = null,
    val previousEndDate: Triple<Int, Int, Int>? = null,
) {
    fun formatDisplayDate(ratingDateModel: RatingDateModel): Pair<Int, Array<Int>> {
        val (currentStartDate, currentEndDate) = ratingDateModel
        return when {
            currentStartDate == null && currentEndDate == null -> R.string.novel_rating_add_date to arrayOf()
            currentStartDate != null && currentEndDate != null ->
                R.string.novel_rating_display_date_with_tilde to
                        arrayOf(
                            currentStartDate.first,
                            currentStartDate.second,
                            currentStartDate.third,
                            currentEndDate.first,
                            currentEndDate.second,
                            currentEndDate.third,
                        )

            currentStartDate != null ->
                R.string.novel_rating_display_date to
                        arrayOf(
                            currentStartDate.first,
                            currentStartDate.second,
                            currentStartDate.third,
                        )

            currentEndDate != null ->
                R.string.novel_rating_display_date to
                        arrayOf(
                            currentEndDate.first,
                            currentEndDate.second,
                            currentEndDate.third,
                        )

            else -> R.string.novel_rating_add_date to arrayOf()
        }
    }

    companion object {
        fun Triple<Int, Int, Int>.toFormattedDate(): String {
            return String.format(Locale.getDefault(), "%04d-%02d-%02d", first, second, third)
        }
    }
}

data class NovelRatingKeywordsModel(
    val categories: List<NovelRatingKeywordCategoryModel> = emptyList(),
    val currentSelectedKeywords: List<NovelRatingKeywordModel> = emptyList(),
    val searchResultKeywords: List<NovelRatingKeywordModel> = emptyList(),
    val isCurrentSelectedKeywordsEmpty: Boolean = currentSelectedKeywords.isEmpty(),
    val isSearchResultKeywordsEmpty: Boolean = searchResultKeywords.isEmpty(),
) {
    fun updatedCategories(keyword: NovelRatingKeywordModel): List<NovelRatingKeywordCategoryModel> {
        return categories.map { category ->
            val updatedKeywords = category.keywords.map { previousKeyword ->
                if (previousKeyword.keywordId == keyword.keywordId) keyword
                else previousKeyword
            }
            category.copy(keywords = updatedKeywords)
        }
    }

    fun updateSelectedKeywords(keyword: NovelRatingKeywordModel, isSelected: Boolean): NovelRatingKeywordsModel {
        val newSelectedKeywords = currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }.toList()

        return this.copy(
            categories = this.updatedCategories(keyword.copy(isSelected = isSelected)),
            currentSelectedKeywords = newSelectedKeywords,
            isCurrentSelectedKeywordsEmpty = newSelectedKeywords.isEmpty()
        )
    }
}

data class NovelRatingKeywordCategoryModel(
    val categoryName: String,
    val categoryImageUrl: String,
    val keywords: List<NovelRatingKeywordModel>,
)

data class NovelRatingKeywordModel(
    val keywordId: Int,
    val keywordName: String,
    val isSelected: Boolean = false,
)
