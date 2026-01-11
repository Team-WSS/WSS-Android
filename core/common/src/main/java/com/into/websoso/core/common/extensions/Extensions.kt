package com.into.websoso.core.common.extensions

fun Float?.orDefault(defaultValue: Float = 0f) = this ?: defaultValue
fun Long?.orDefault(defaultValue: Long = 0L) = this ?: defaultValue
fun Int?.orDefault(defaultValue: Int = 0) = this ?: defaultValue
