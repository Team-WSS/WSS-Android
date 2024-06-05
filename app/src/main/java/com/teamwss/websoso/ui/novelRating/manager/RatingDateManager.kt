package com.teamwss.websoso.ui.novelRating.manager

import android.util.Log
import com.teamwss.websoso.ui.novelRating.model.Month
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import java.time.LocalDate

class RatingDateManager {
    private val today = LocalDate.now()

    fun updateReadStatus(
        novelRatingModel: NovelRatingModel,
        readStatus: ReadStatus
    ): NovelRatingModel {
        val ratingDateModel = novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate == null && ratingDateModel.currentEndDate == null || novelRatingModel.uiReadStatus == readStatus) {
            return novelRatingModel.copy(uiReadStatus = readStatus)
        }

        when (readStatus) {
            ReadStatus.WATCHING -> handleWatchingStatus(ratingDateModel)
            ReadStatus.WATCHED -> handleWatchedStatus(ratingDateModel)
            ReadStatus.QUIT -> handleQuitStatus(ratingDateModel)
        }

        return novelRatingModel.copy(ratingDateModel = ratingDateModel, uiReadStatus = readStatus)
    }

    private fun handleWatchingStatus(ratingDateModel: RatingDateModel) {
        ratingDateModel.currentStartDate = when (ratingDateModel.pastStartDate == null) {
            true -> today.toFormattedDate()
            false -> ratingDateModel.pastStartDate
        }
        ratingDateModel.pastEndDate = ratingDateModel.currentEndDate
        ratingDateModel.currentEndDate = null
    }

    private fun handleWatchedStatus(ratingDateModel: RatingDateModel) {
        ratingDateModel.currentStartDate = when (ratingDateModel.pastStartDate == null) {
            true -> today.toFormattedDate()
            false -> ratingDateModel.pastStartDate
        }
        ratingDateModel.currentEndDate = when (ratingDateModel.pastEndDate == null) {
            true -> today.toFormattedDate()
            false -> ratingDateModel.pastEndDate
        }
        checkIsStartAfterEnd(ratingDateModel, ReadStatus.WATCHED, isEditingStartDate = false)
    }

    private fun handleQuitStatus(ratingDateModel: RatingDateModel) {
        ratingDateModel.currentEndDate = when (ratingDateModel.pastEndDate == null) {
            true -> today.toFormattedDate()
            false -> ratingDateModel.pastEndDate
        }
        ratingDateModel.pastStartDate = ratingDateModel.currentStartDate
        ratingDateModel.currentStartDate = null
    }

    fun updateIsEditingStartDate(readStatus: ReadStatus): Boolean {
        return when (readStatus) {
            ReadStatus.WATCHING -> true
            ReadStatus.WATCHED -> true
            ReadStatus.QUIT -> false
        }
    }

    fun updateDayMaxValue(ratingDateModel: RatingDateModel, isEditingStartDate: Boolean): Int {
        return when (isEditingStartDate) {
            true -> Month.getDays(
                ratingDateModel.currentStartDate?.first ?: today.year,
                ratingDateModel.currentStartDate?.second ?: today.monthValue
            )

            false -> Month.getDays(
                ratingDateModel.currentEndDate?.first ?: today.year,
                ratingDateModel.currentEndDate?.second ?: today.monthValue
            )
        }
    }

    fun updateCurrentDate(
        novelRatingModel: NovelRatingModel,
        date: Triple<Int, Int, Int>,
        isEditingStartDate: Boolean
    ): RatingDateModel {
        var updatedDateModel = when (isEditingStartDate) {
            true -> novelRatingModel.ratingDateModel.copy(currentStartDate = date)
            false -> novelRatingModel.ratingDateModel.copy(currentEndDate = date)
        }
        if (updatedDateModel.currentStartDate == null && updatedDateModel.currentEndDate == null) return updatedDateModel

        updatedDateModel = checkIsTodayAfterToday(updatedDateModel, isEditingStartDate)
        updatedDateModel = checkIsStartAfterEnd(
            updatedDateModel,
            novelRatingModel.uiReadStatus,
            isEditingStartDate
        )

        return updatedDateModel
    }

    private fun checkIsTodayAfterToday(
        ratingDateModel: RatingDateModel,
        isEditingStartDate: Boolean
    ): RatingDateModel {
        when (isEditingStartDate) {
            true -> ratingDateModel.currentStartDate =
                validateIsAfterToday(ratingDateModel.currentStartDate)

            false -> ratingDateModel.currentEndDate =
                validateIsAfterToday(ratingDateModel.currentEndDate)
        }
        return ratingDateModel
    }

    private fun validateIsAfterToday(
        date: Triple<Int, Int, Int>?
    ): Triple<Int, Int, Int> {
        return date?.updateNotAfter(today.toFormattedDate()) ?: today.toFormattedDate()
    }

    private fun Triple<Int, Int, Int>.updateNotAfter(other: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
        return when {
            this.first > other.first -> Triple(other.first, this.second, this.third)
            this.first == other.first && this.second > other.second -> Triple(
                this.first,
                other.second,
                this.third
            )

            this.first == other.first && this.second == other.second && this.third > other.third -> Triple(
                this.first,
                this.second,
                other.third
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
        isEditingStartDate: Boolean
    ): RatingDateModel {
        if (readStatus != ReadStatus.WATCHED) return ratingDateModel

        when (isEditingStartDate) {
            true -> ratingDateModel.currentStartDate =
                validateIsStartAfterEnd(
                    ratingDateModel.currentStartDate!!,
                    ratingDateModel.currentEndDate!!
                )

            false -> if (ratingDateModel.currentStartDate.toLocalDate()
                    .isAfter(ratingDateModel.currentEndDate.toLocalDate())
            ) ratingDateModel.currentStartDate = ratingDateModel.currentEndDate
        }
        return ratingDateModel
    }

    private fun Triple<Int, Int, Int>?.toLocalDate(): LocalDate {
        return LocalDate.of(this!!.first, this.second, this.third)
    }

    private fun validateIsStartAfterEnd(
        startDate: Triple<Int, Int, Int>,
        endDate: Triple<Int, Int, Int>
    ): Triple<Int, Int, Int> {
        return startDate.updateNotAfter(endDate)
    }

    fun clearCurrentDate(
        ratingDateModel: RatingDateModel
    ): RatingDateModel {
        return ratingDateModel.copy(
            currentStartDate = null,
            currentEndDate = null,
            pastStartDate = null,
            pastEndDate = null
        )
    }

    fun cancelDateEdit(
        ratingDateModel: RatingDateModel
    ): RatingDateModel {
        if (ratingDateModel.currentStartDate != null) {
            ratingDateModel.currentStartDate = ratingDateModel.pastStartDate
            Log.e("1231231", "currentStartDate: ${ratingDateModel.currentStartDate} pastStartDate: ${ratingDateModel.pastStartDate}")
        }
        if (ratingDateModel.currentEndDate != null) {
            ratingDateModel.currentEndDate = ratingDateModel.pastEndDate
            Log.e("1231232", "currentEndDate: ${ratingDateModel.currentEndDate} pastEndDate: ${ratingDateModel.pastEndDate}")
        }
        return ratingDateModel
    }

    fun createNotNullDate(
        novelRatingModel: NovelRatingModel
    ): RatingDateModel {
        val ratingDateModel = novelRatingModel.ratingDateModel
        when (novelRatingModel.uiReadStatus) {
            ReadStatus.WATCHING -> {
                ratingDateModel.currentStartDate =
                    ratingDateModel.currentStartDate ?: today.toFormattedDate()
            }

            ReadStatus.WATCHED -> {
                ratingDateModel.currentStartDate =
                    ratingDateModel.currentStartDate ?: today.toFormattedDate()
                ratingDateModel.currentEndDate =
                    ratingDateModel.currentEndDate ?: today.toFormattedDate()
            }

            ReadStatus.QUIT -> {
                ratingDateModel.currentEndDate =
                    ratingDateModel.currentEndDate ?: today.toFormattedDate()
            }
        }
        return ratingDateModel
    }
}