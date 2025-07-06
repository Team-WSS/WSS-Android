package com.into.websoso.feature.library.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd")

fun formatDateRange(
    startDate: String?,
    endDate: String?,
): String? {
    if (startDate.isNullOrBlank()) return null

    return try {
        val startFormatted =
            LocalDate.parse(startDate, INPUT_DATE_FORMATTER).format(OUTPUT_DATE_FORMATTER)

        if (endDate.isNullOrBlank()) {
            startFormatted
        } else {
            val endFormatted =
                LocalDate.parse(endDate, INPUT_DATE_FORMATTER).format(OUTPUT_DATE_FORMATTER)
            "$startFormatted ~ $endFormatted"
        }
    } catch (e: DateTimeParseException) {
        null
    }
}
