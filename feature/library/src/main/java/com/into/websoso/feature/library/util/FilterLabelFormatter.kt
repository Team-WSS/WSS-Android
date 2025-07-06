package com.into.websoso.feature.library.util

fun buildFilterLabel(
    defaultLabel: String,
    values: List<String>,
): String =
    when {
        values.isEmpty() -> defaultLabel
        values.size == 1 -> values.first()
        else -> "${values.first()} 외 ${values.size - 1}개"
    }
