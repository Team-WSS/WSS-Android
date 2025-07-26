package com.into.websoso.core.common.extensions

fun Float.isCloseTo(
    target: Float,
    epsilon: Float = 0.01f,
): Boolean = kotlin.math.abs(this - target) < epsilon
