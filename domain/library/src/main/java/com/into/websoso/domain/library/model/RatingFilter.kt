package com.into.websoso.domain.library.model

import java.util.Locale

data class RatingFilter(
    val min: Float = RATING_MIN,
    val max: Float = RATING_MAX,
    val isRatingless: Boolean = false,
) {
    val isSelected: Boolean
        get() = isRatingless || min > RATING_MIN || max < RATING_MAX

    val chipLabel: String
        get() = if (isRatingless) {
            RATINGLESS_LABEL
        } else {
            "${min.toRatingText()}~${max.toRatingText()}"
        }

    fun setRange(
        newMin: Float,
        newMax: Float,
    ): RatingFilter =
        copy(
            min = newMin.coerceIn(RATING_MIN, RATING_MAX),
            max = newMax.coerceIn(RATING_MIN, RATING_MAX),
            isRatingless = false,
        )

    fun toggleRatingless(): RatingFilter =
        if (isRatingless) {
            RatingFilter()
        } else {
            RatingFilter(isRatingless = true)
        }

    private fun Float.toRatingText(): String = String.format(Locale.US, "%.1f", this)

    companion object {
        const val RATING_MIN = 0.0f
        const val RATING_MAX = 5.0f
        const val RATING_STEP = 0.5f
        private const val RATINGLESS_LABEL = "별점 없음"
    }
}
