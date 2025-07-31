package com.into.websoso.domain.library.model

import com.into.websoso.core.common.extensions.isCloseTo

enum class Rating(
    val value: Float,
) {
    DEFAULT(0.0f),
    THREE_POINT_FIVE(3.5f),
    FOUR(4.0f),
    FOUR_POINT_FIVE(4.5f),
    FOUR_POINT_EIGHT(4.8f),
    ;

    companion object {
        fun from(value: Float): Rating = entries.find { it.value.isCloseTo(value) } ?: DEFAULT
    }
}
