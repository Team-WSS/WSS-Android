package com.into.websoso.domain.library.model

import com.into.websoso.core.common.extensions.isCloseTo

enum class Rating(
    val value: Float,
) {
    DEFAULT(0.0f),
    POINT_FIVE(0.5f),
    ONE(1.0f),
    ONE_POINT_FIVE(1.5f),
    TWO(2.0f),
    TWO_POINT_FIVE(2.5f),
    THREE(3.0f),
    THREE_POINT_FIVE(3.5f),
    FOUR(4.0f),
    FOUR_POINT_FIVE(4.5f),
    FOUR_POINT_EIGHT(4.8f),
    FIVE(5.0f),
    ;

    companion object {
        fun from(value: Float): Rating =
            entries.find {
                it.value.isCloseTo(value)
            } ?: DEFAULT
    }
}
