package com.into.websoso.ui.novelRating.model

enum class Month(val month: Int, val days: Int, val daysInLeapYear: Int? = null) {
    FEBRUARY(2, 28, 29),
    APRIL(4, 30),
    JUNE(6, 30),
    SEPTEMBER(9, 30),
    NOVEMBER(11, 30),
    ETC(0, 31);

    companion object {
        fun getDays(year: Int, month: Int): Int {
            val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
            return when (month) {
                2 -> if (isLeapYear) FEBRUARY.daysInLeapYear ?: 29 else FEBRUARY.days
                4 -> APRIL.days
                6 -> JUNE.days
                9 -> SEPTEMBER.days
                11 -> NOVEMBER.days
                else -> ETC.days
            }
        }
    }
}
