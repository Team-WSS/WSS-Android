package com.into.websoso.domain.library.model

import com.into.websoso.core.common.extensions.isCloseTo

data class NovelRating(
    val rating: Rating = Rating.DEFAULT,
) {
    val isSelected: Boolean
        get() = rating != Rating.DEFAULT

    fun isCloseTo(other: Rating): Boolean = rating.value.isCloseTo(other.value)

    fun set(newRating: Rating): NovelRating =
        if (rating == newRating) {
            copy(rating = Rating.DEFAULT)
        } else {
            copy(rating = newRating)
        }

    companion object {
        fun from(value: Float): NovelRating = NovelRating(Rating.from(value))
    }
}
