package com.into.websoso.feature.library.model

import com.into.websoso.core.common.extensions.formatDateRange
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.NovelRating

internal data class NovelUiModel(
    val novelId: Long,
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val novelImage: String,
    val readStatus: ReadStatusUiModel?,
    val userNovelRating: NovelRating?,
    val novelRating: NovelRating,
    val attractivePoints: AttractivePoints,
    val keywords: List<String>,
    val myFeeds: List<String>,
    val isInterest: Boolean,
    val formattedDateRange: String? = formatDateRange(startDate, endDate),
) {
    val ratingStars: List<RatingStarUiModel> = calculateRatingStars()

    private fun calculateRatingStars(): List<RatingStarUiModel> {
        if (userNovelRating == null) return emptyList()

        val fullStar = userNovelRating.rating.value.toInt()
        val halfStar = (userNovelRating.rating.value - fullStar) >= 0.5f
        val emptyStar = 5 - fullStar - if (halfStar) 1 else 0

        return buildList {
            repeat(fullStar) { add(RatingStarUiModel.FULL) }
            if (halfStar) add(RatingStarUiModel.HALF)
            repeat(emptyStar) { add(RatingStarUiModel.EMPTY) }
        }
    }
}
