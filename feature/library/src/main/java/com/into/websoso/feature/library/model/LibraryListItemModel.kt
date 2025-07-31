package com.into.websoso.feature.library.model

import com.into.websoso.core.common.extensions.formatDateRange
import com.into.websoso.domain.library.model.AttractivePoints

data class LibraryListItemModel(
    val novelId: Long,
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val novelImage: String,
    val readStatus: ReadStatusUiModel?,
    val userNovelRating: Float?,
    val novelRating: Float,
    val attractivePoints: AttractivePoints,
    val keywords: List<String>,
    val myFeeds: List<String>,
    val isInterest: Boolean,
    val formattedDateRange: String? = formatDateRange(startDate, endDate),
) {
    val ratingStars: List<RatingStarType> = calculateRatingStars(userNovelRating)

    private fun calculateRatingStars(rating: Float?): List<RatingStarType> {
        if (rating == null) return emptyList()

        val fullStar = rating.toInt()
        val halfStar = (rating - fullStar) >= 0.5f
        val emptyStar = 5 - fullStar - if (halfStar) 1 else 0

        return buildList {
            repeat(fullStar) { add(RatingStarType.FULL) }
            if (halfStar) add(RatingStarType.HALF)
            repeat(emptyStar) { add(RatingStarType.EMPTY) }
        }
    }
}

enum class RatingStarType {
    FULL,
    HALF,
    EMPTY,
}
