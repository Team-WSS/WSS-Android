package com.into.websoso.core.common.extensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yy.MM.dd")

fun formatDateRange(
    startDate: String?,
    endDate: String?,
): String? {
    return try {
        val startFormatted = startDate
            ?.takeIf { it.isNotBlank() }
            ?.let { LocalDate.parse(it, INPUT_DATE_FORMATTER).format(OUTPUT_DATE_FORMATTER) }
        val endFormatted = endDate
            ?.takeIf { it.isNotBlank() }
            ?.let { LocalDate.parse(it, INPUT_DATE_FORMATTER).format(OUTPUT_DATE_FORMATTER) }

        when {
            startFormatted != null && endFormatted != null -> "$startFormatted ~ $endFormatted"
            startFormatted != null -> startFormatted
            endFormatted != null -> endFormatted
            else -> null
        }
    } catch (e: DateTimeParseException) {
        null
    }
}
