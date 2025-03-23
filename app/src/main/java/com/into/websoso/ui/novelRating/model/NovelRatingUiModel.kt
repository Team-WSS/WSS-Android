package com.into.websoso.ui.novelRating.model

import com.into.websoso.core.common.ui.model.CategoriesModel
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.resource.R.string.novel_rating_add_date
import com.into.websoso.resource.R.string.novel_rating_display_date
import com.into.websoso.resource.R.string.novel_rating_display_date_with_tilde
import com.into.websoso.ui.novelRating.model.ReadStatus.WATCHING
import java.util.Locale

data class NovelRatingModel(
    val novelTitle: String = "",
    val readStatus: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val userNovelRating: Float = 0f,
    val charmPoints: List<CharmPoint> = emptyList(),
    val isCharmPointExceed: Boolean = false,
    val userKeywords: Set<KeywordModel> = setOf(),
    val uiReadStatus: ReadStatus = ReadStatus.valueOf(readStatus ?: WATCHING.name),
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

        fun String.toCharmPoint(): CharmPoint =
            CharmPoint.entries.find { it.value == this } ?: CharmPoint.WORLDVIEW
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
            currentStartDate == null && currentEndDate == null -> novel_rating_add_date to arrayOf()
            currentStartDate != null && currentEndDate != null ->
                novel_rating_display_date_with_tilde to
                        arrayOf(
                            currentStartDate.first,
                            currentStartDate.second,
                            currentStartDate.third,
                            currentEndDate.first,
                            currentEndDate.second,
                            currentEndDate.third,
                        )

            currentStartDate != null ->
                novel_rating_display_date to
                        arrayOf(
                            currentStartDate.first,
                            currentStartDate.second,
                            currentStartDate.third,
                        )

            currentEndDate != null ->
                novel_rating_display_date to
                        arrayOf(
                            currentEndDate.first,
                            currentEndDate.second,
                            currentEndDate.third,
                        )

            else -> novel_rating_add_date to arrayOf()
        }
    }

    companion object {
        fun Triple<Int, Int, Int>.toFormattedDate(): String {
            return String.format(Locale.getDefault(), "%04d-%02d-%02d", first, second, third)
        }
    }
}

data class NovelRatingKeywordsModel(
    val categories: List<CategoriesModel.CategoryModel> = emptyList(),
    val currentSelectedKeywords: Set<KeywordModel> = mutableSetOf(),
    val isCurrentSelectedKeywordsEmpty: Boolean = currentSelectedKeywords.isEmpty(),
    val isSearchKeywordProceeding: Boolean = false,
    val isInitialSearchKeyword: Boolean = true,
    val searchResultKeywords: List<KeywordModel> = emptyList(),
    val isSearchResultKeywordsEmpty: Boolean = false,
    val isSearchKeywordExceed: Boolean = false,
) {
    private fun updatedCategories(keyword: KeywordModel): List<CategoriesModel.CategoryModel> {
        return categories.map { category ->
            val updatedKeywords = category.keywords.map { previousKeyword ->
                if (previousKeyword.keywordId == keyword.keywordId) keyword
                else previousKeyword
            }
            category.copy(keywords = updatedKeywords)
        }
    }

    fun updateSelectedKeywords(
        keyword: KeywordModel,
        isSelected: Boolean,
    ): NovelRatingKeywordsModel {
        val newSelectedKeywords = currentSelectedKeywords.toMutableSet().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }

        return this.copy(
            categories = updatedCategories(keyword.copy(isSelected = isSelected)),
            currentSelectedKeywords = newSelectedKeywords,
            isCurrentSelectedKeywordsEmpty = newSelectedKeywords.isEmpty(),
            isSearchKeywordExceed = newSelectedKeywords.size > MAX_KEYWORD_COUNT && isSelected,
        )
    }

    companion object {
        private const val MAX_KEYWORD_COUNT = 20
    }
}
