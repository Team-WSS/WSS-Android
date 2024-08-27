package com.teamwss.websoso.ui.novelRating.util

import com.teamwss.websoso.ui.novelRating.model.Month
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import java.time.LocalDate

class RatingDateManager {
    private val today = LocalDate.now()

    fun updateReadStatus(
        novelRatingModel: NovelRatingModel,
        readStatus: ReadStatus,
    ): NovelRatingModel {
        val ratingDateModel = novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate == null && ratingDateModel.currentEndDate == null || novelRatingModel.uiReadStatus == readStatus) {
            return novelRatingModel.copy(uiReadStatus = readStatus)
        }

        val updatedRatingDateModel = when (readStatus) {
            ReadStatus.WATCHING -> handleWatchingStatus(ratingDateModel)
            ReadStatus.WATCHED -> handleWatchedStatus(ratingDateModel)
            ReadStatus.QUIT -> handleQuitStatus(ratingDateModel)
            ReadStatus.NONE -> ratingDateModel
        }

        return novelRatingModel.copy(
            ratingDateModel = updatedRatingDateModel,
            uiReadStatus = readStatus,
        )
    }

    private fun handleWatchingStatus(ratingDateModel: RatingDateModel): RatingDateModel {
        return ratingDateModel.copy(
            currentStartDate = when (ratingDateModel.previousStartDate == null) {
                true -> today.toFormattedDate()
                false -> ratingDateModel.previousStartDate
            },
            previousEndDate = ratingDateModel.currentEndDate,
            currentEndDate = null
        )
    }

    private fun handleWatchedStatus(ratingDateModel: RatingDateModel): RatingDateModel {
        val updatedRatingDateModel = ratingDateModel.copy(
            currentStartDate = when (ratingDateModel.previousStartDate == null) {
                true -> today.toFormattedDate()
                false -> ratingDateModel.previousStartDate
            },
            currentEndDate = when (ratingDateModel.previousEndDate == null) {
                true -> today.toFormattedDate()
                false -> ratingDateModel.previousEndDate
            }
        )
        checkIsStartAfterEnd(updatedRatingDateModel, ReadStatus.WATCHED, isEditingStartDate = false)
        return updatedRatingDateModel
    }

    private fun handleQuitStatus(ratingDateModel: RatingDateModel): RatingDateModel {
        return ratingDateModel.copy(
            currentEndDate = when (ratingDateModel.previousEndDate == null) {
                true -> today.toFormattedDate()
                false -> ratingDateModel.previousEndDate
            },
            previousStartDate = ratingDateModel.currentStartDate,
            currentStartDate = null
        )
    }

    fun updateIsEditingStartDate(readStatus: ReadStatus): Boolean {
        return when (readStatus) {
            ReadStatus.WATCHING -> true
            ReadStatus.WATCHED -> true
            ReadStatus.QUIT -> false
            else -> true
        }
    }

    fun updateDayMaxValue(
        ratingDateModel: RatingDateModel,
        isEditingStartDate: Boolean,
    ): Int {
        return when (isEditingStartDate) {
            true ->
                Month.getDays(
                    ratingDateModel.currentStartDate?.first ?: today.year,
                    ratingDateModel.currentStartDate?.second ?: today.monthValue,
                )

            false ->
                Month.getDays(
                    ratingDateModel.currentEndDate?.first ?: today.year,
                    ratingDateModel.currentEndDate?.second ?: today.monthValue,
                )
        }
    }

    fun updateCurrentDate(
        novelRatingModel: NovelRatingModel,
        date: Triple<Int, Int, Int>,
        isEditingStartDate: Boolean,
    ): RatingDateModel {
        var updatedDateModel =
            when (isEditingStartDate) {
                true -> novelRatingModel.ratingDateModel.copy(currentStartDate = date)
                false -> novelRatingModel.ratingDateModel.copy(currentEndDate = date)
            }
        if (updatedDateModel.currentStartDate == null && updatedDateModel.currentEndDate == null) return updatedDateModel

        updatedDateModel = checkIsTodayAfterToday(updatedDateModel, isEditingStartDate)
        updatedDateModel =
            checkIsStartAfterEnd(
                updatedDateModel,
                novelRatingModel.uiReadStatus,
                isEditingStartDate,
            )

        return updatedDateModel
    }

    private fun checkIsTodayAfterToday(
        ratingDateModel: RatingDateModel,
        isEditingStartDate: Boolean,
    ): RatingDateModel {
        return when (isEditingStartDate) {
            true -> ratingDateModel.copy(
                currentStartDate = validateIsAfterToday(ratingDateModel.currentStartDate)
            )

            false -> ratingDateModel.copy(
                currentEndDate = validateIsAfterToday(ratingDateModel.currentEndDate)
            )
        }
    }

    private fun validateIsAfterToday(date: Triple<Int, Int, Int>?): Triple<Int, Int, Int> {
        return date?.updateNotAfter(today.toFormattedDate()) ?: today.toFormattedDate()
    }

    private fun Triple<Int, Int, Int>.updateNotAfter(other: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return when {
            this.first > other.first -> Triple(other.first, this.second, this.third)
            this.first == other.first && this.second > other.second ->
                Triple(
                    this.first,
                    other.second,
                    this.third,
                )

            this.first == other.first && this.second == other.second && this.third > other.third ->
                Triple(
                    this.first,
                    this.second,
                    other.third,
                )

            else -> this
        }
    }

    private fun LocalDate.toFormattedDate(): Triple<Int, Int, Int> {
        return Triple(this.year, this.monthValue, this.dayOfMonth)
    }

    private fun checkIsStartAfterEnd(
        ratingDateModel: RatingDateModel,
        readStatus: ReadStatus,
        isEditingStartDate: Boolean,
    ): RatingDateModel {
        if (readStatus != ReadStatus.WATCHED) return ratingDateModel

        return when (isEditingStartDate) {
            true -> ratingDateModel.copy(
                currentStartDate = validateIsStartAfterEnd(
                    ratingDateModel.currentStartDate ?: today.toFormattedDate(),
                    ratingDateModel.currentEndDate ?: today.toFormattedDate(),
                )
            )

            false -> if (ratingDateModel.currentStartDate.toLocalDate()
                    .isAfter(ratingDateModel.currentEndDate.toLocalDate())
            ) {
                ratingDateModel.copy(
                    currentStartDate = ratingDateModel.currentEndDate
                )
            } else ratingDateModel
        }
    }

    private fun Triple<Int, Int, Int>?.toLocalDate(): LocalDate {
        return LocalDate.of(this?.first ?: 1, this?.second ?: 1, this?.third ?: 1)
    }

    private fun validateIsStartAfterEnd(
        startDate: Triple<Int, Int, Int>,
        endDate: Triple<Int, Int, Int>,
    ): Triple<Int, Int, Int> {
        return startDate.updateNotAfter(endDate)
    }

    fun clearCurrentDate(ratingDateModel: RatingDateModel): RatingDateModel {
        return ratingDateModel.copy(
            currentStartDate = null,
            currentEndDate = null,
            previousStartDate = null,
            previousEndDate = null,
        )
    }

    fun cancelDateEdit(ratingDateModel: RatingDateModel): RatingDateModel {
        return ratingDateModel.copy(
            currentStartDate = if (ratingDateModel.currentStartDate != null) {
                ratingDateModel.previousStartDate
            } else null,
            currentEndDate = if (ratingDateModel.currentEndDate != null) {
                ratingDateModel.previousEndDate
            } else null,
        )
    }


    fun getNotNullDate(novelRatingModel: NovelRatingModel): RatingDateModel {
        val ratingDateModel = novelRatingModel.ratingDateModel
        return when (novelRatingModel.uiReadStatus) {
            ReadStatus.WATCHING -> ratingDateModel.copy(
                currentStartDate = ratingDateModel.currentStartDate ?: today.toFormattedDate()
            )

            ReadStatus.WATCHED -> ratingDateModel.copy(
                currentStartDate = ratingDateModel.currentStartDate ?: today.toFormattedDate(),
                currentEndDate = ratingDateModel.currentEndDate ?: today.toFormattedDate()
            )

            ReadStatus.QUIT -> ratingDateModel.copy(
                currentEndDate = ratingDateModel.currentEndDate ?: today.toFormattedDate()
            )

            ReadStatus.NONE -> ratingDateModel
        }
    }
}
