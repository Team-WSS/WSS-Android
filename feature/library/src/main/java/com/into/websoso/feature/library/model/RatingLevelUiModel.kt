package com.into.websoso.feature.library.model

enum class RatingLevelUiModel(
    val value: Float,
) {
    THREE_POINT_FIVE(3.5f),
    FOUR(4.0f),
    FOUR_POINT_FIVE(4.5f),
    FOUR_POINT_EIGHT(4.8f),
    ;

    companion object {
        internal fun Float.isCloseTo(
            target: Float,
            epsilon: Float = 0.01f,
        ): Boolean = kotlin.math.abs(this - target) < epsilon
    }
}
