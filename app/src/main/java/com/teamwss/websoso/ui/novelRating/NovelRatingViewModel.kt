package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.novelRating.model.Month
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
import com.teamwss.websoso.ui.novelRating.model.DatePickerType
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import java.time.LocalDate

class NovelRatingViewModel : ViewModel() {
    private val _maxDayValue = MutableLiveData<Int>()
    val maxDayValue: LiveData<Int> get() = _maxDayValue

    private val _uiState = MutableLiveData<NovelRatingUiState>()
    val uiState: LiveData<NovelRatingUiState> get() = _uiState

    private val _isEditingStartDate = MutableLiveData<Boolean>()
    val isEditingStartDate: LiveData<Boolean> get() = _isEditingStartDate

    private var pastStartDate: Triple<Int, Int, Int>? = null
    private var pastEndDate: Triple<Int, Int, Int>? = null

    // 테스트용 함수
    fun getUserNovelInfo() {
        _uiState.value = NovelRatingUiState(
            novelRatingModel = NovelRatingModel(
                userNovelId = 1,
                novelTitle = "철혈검가 사냥개의 회귀",
                userNovelRating = 4.0f,
                readStatus = "WATCHED",
                startDate = "2023-02-28",
                endDate = "2024-05-11"
            )
        )
        updateIsEditingStartDate(ReadStatus.WATCHING)
        updatePastDate()
        updateDayMaxValue()
    }

    private fun updateIsEditingStartDate(readStatus: ReadStatus) {
        when (readStatus) {
            ReadStatus.WATCHING -> _isEditingStartDate.value = true
            ReadStatus.WATCHED -> _isEditingStartDate.value = true
            ReadStatus.QUIT -> _isEditingStartDate.value = false
        }
    }

    fun updatePastDate() {
        val uiState = uiState.value ?: return
        pastStartDate = uiState.novelRatingModel.currentStartDate
        pastEndDate = uiState.novelRatingModel.currentEndDate
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        val uiState = uiState.value ?: return
        if (uiState.novelRatingModel.uiReadStatus == readStatus) return
        when (readStatus) {
            ReadStatus.WATCHING -> handleWatchingState(uiState)
            ReadStatus.WATCHED -> handleWatchedState(uiState)
            ReadStatus.QUIT -> handleQuitState(uiState)
        }

        uiState.novelRatingModel.uiReadStatus = readStatus
        _uiState.value = uiState
        updateIsEditingStartDate(readStatus)
    }

    private fun handleWatchingState(uiState: NovelRatingUiState) {
        val novelRatingModel = uiState.novelRatingModel
        if (novelRatingModel.currentStartDate == null && novelRatingModel.currentEndDate == null) return
        novelRatingModel.currentStartDate = when (pastStartDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> pastStartDate
        }
        pastEndDate = novelRatingModel.currentEndDate
        novelRatingModel.currentEndDate = null
    }

    private fun handleWatchedState(uiState: NovelRatingUiState) {
        val novelRatingModel = uiState.novelRatingModel
        if (novelRatingModel.currentStartDate == null && novelRatingModel.currentEndDate == null) return
        novelRatingModel.currentStartDate = when (pastStartDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> pastStartDate
        }
        novelRatingModel.currentEndDate = when (pastEndDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> pastEndDate
        }
        checkValidityIsStartAfterEnd()
    }

    private fun handleQuitState(uiState: NovelRatingUiState) {
        val novelRatingModel = uiState.novelRatingModel
        if (novelRatingModel.currentStartDate == null && novelRatingModel.currentEndDate == null) return
        novelRatingModel.currentEndDate = when (pastEndDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> pastEndDate
        }
        pastStartDate = novelRatingModel.currentStartDate
        novelRatingModel.currentStartDate = null
    }

    private fun LocalDate.toFormattedDate(): Triple<Int, Int, Int> {
        return Triple(this.year, this.monthValue, this.dayOfMonth)
    }

    private fun updateStartDate(date: Triple<Int, Int, Int>) {
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.currentStartDate = date
        _uiState.value = uiState
        updateDayMaxValue()
        updateDateValidity()
    }

    private fun updateEndDate(date: Triple<Int, Int, Int>) {
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.currentEndDate = date
        _uiState.value = uiState
        updateDayMaxValue()
        updateDateValidity()
    }

    fun updateDayMaxValue() {
        val novelRatingModel = uiState.value?.novelRatingModel ?: return
        _maxDayValue.value = when (isEditingStartDate.value) {
            true -> Month.getDays(
                novelRatingModel.currentStartDate?.first ?: LocalDate.now().year,
                novelRatingModel.currentStartDate?.second ?: LocalDate.now().monthValue
            )

            false -> Month.getDays(
                novelRatingModel.currentEndDate?.first ?: LocalDate.now().year,
                novelRatingModel.currentEndDate?.second ?: LocalDate.now().monthValue
            )

            else -> 31
        }
    }

    fun toggleEditingStartDate(boolean: Boolean) {
        _isEditingStartDate.value = boolean
    }

    fun updateCurrentDate(date: Triple<Int, Int, Int>) {
        when (isEditingStartDate.value) {
            true -> updateStartDate(date)
            false -> updateEndDate(date)
            else -> {}
        }
    }

    private fun updateDateValidity() {
        checkValidityIsAfterToday()
        if (uiState.value?.novelRatingModel?.uiReadStatus == ReadStatus.WATCHED) checkValidityIsStartAfterEnd()
    }

    private fun checkValidityIsAfterToday() {
        val startLocalDate = uiState.value?.novelRatingModel?.currentStartDate.toLocalDate()
        val endLocalDate = uiState.value?.novelRatingModel?.currentEndDate.toLocalDate()

        updateDateIfAfterToday(startLocalDate, isEditingStartDate = true)
        updateDateIfAfterToday(endLocalDate, isEditingStartDate = false)
    }

    private fun updateDateIfAfterToday(
        dateToCheck: LocalDate,
        isEditingStartDate: Boolean
    ) {
        val today = LocalDate.now()
        when {
            dateToCheck.year > today.year -> updateDateToToday(
                isEditingStartDate,
                DatePickerType.YEAR
            )

            dateToCheck.year == today.year && dateToCheck.monthValue > today.monthValue ->
                updateDateToToday(isEditingStartDate, DatePickerType.MONTH)

            dateToCheck.year == today.year && dateToCheck.monthValue == today.monthValue && dateToCheck.dayOfMonth > today.dayOfMonth ->
                updateDateToToday(isEditingStartDate, DatePickerType.DAY)
        }
    }

    private fun updateDateToToday(isEditingStartDate: Boolean, datePickerType: DatePickerType) {
        val novelRatingModel = uiState.value?.novelRatingModel ?: return
        val today = LocalDate.now()

        val (year, month, day) = when (datePickerType) {
            DatePickerType.YEAR -> Triple(today.year, null, null)
            DatePickerType.MONTH -> Triple(null, today.monthValue, null)
            DatePickerType.DAY -> Triple(null, null, today.dayOfMonth)
        }

        val newDate = Triple(
            year
                ?: (if (isEditingStartDate) novelRatingModel.currentStartDate else novelRatingModel.currentEndDate)?.first
                ?: today.year,
            month
                ?: (if (isEditingStartDate) novelRatingModel.currentStartDate else novelRatingModel.currentEndDate)?.second
                ?: today.monthValue,
            day
                ?: (if (isEditingStartDate) novelRatingModel.currentStartDate else novelRatingModel.currentEndDate)?.third
                ?: today.dayOfMonth
        )

        when (isEditingStartDate) {
            true -> updateStartDate(newDate)
            false -> updateEndDate(newDate)
        }
    }


    private fun Triple<Int, Int, Int>?.toLocalDate(): LocalDate {
        if (this == null) return LocalDate.now()
        return when (this.third <= Month.getDays(this.first, this.second)) {
            true -> return LocalDate.of(this.first, this.second, this.third)
            false -> LocalDate.of(this.first, this.second, Month.getDays(this.first, this.second))
        }
    }

    private fun checkValidityIsStartAfterEnd() {
        val uiState = uiState.value ?: return
        val startLocalDate = uiState.novelRatingModel.currentStartDate.toLocalDate()
        val endLocalDate = uiState.novelRatingModel.currentEndDate.toLocalDate()
        if (!startLocalDate.isAfter(endLocalDate)) return

        when {
            startLocalDate.year > endLocalDate.year -> resetWatchedReadDate(DatePickerType.YEAR)
            startLocalDate.year == endLocalDate.year && startLocalDate.monthValue > endLocalDate.monthValue ->
                resetWatchedReadDate(DatePickerType.MONTH)

            startLocalDate.year == endLocalDate.year && startLocalDate.monthValue == endLocalDate.monthValue && startLocalDate.dayOfMonth > endLocalDate.dayOfMonth -> {
                resetWatchedReadDate(DatePickerType.DAY)
            }
        }
    }

    private fun resetWatchedReadDate(datePickerType: DatePickerType) {
        val today = LocalDate.now()
        val validDate = when (datePickerType) {
            DatePickerType.YEAR -> Triple(
                uiState.value?.novelRatingModel?.currentEndDate?.first ?: today.year,
                uiState.value?.novelRatingModel?.currentStartDate?.second ?: today.monthValue,
                uiState.value?.novelRatingModel?.currentStartDate?.third ?: today.dayOfMonth
            )

            DatePickerType.MONTH -> Triple(
                uiState.value?.novelRatingModel?.currentStartDate?.first ?: today.year,
                uiState.value?.novelRatingModel?.currentEndDate?.second ?: today.monthValue,
                uiState.value?.novelRatingModel?.currentStartDate?.third ?: today.dayOfMonth
            )

            DatePickerType.DAY -> Triple(
                uiState.value?.novelRatingModel?.currentStartDate?.first ?: today.year,
                uiState.value?.novelRatingModel?.currentStartDate?.second ?: today.monthValue,
                uiState.value?.novelRatingModel?.currentEndDate?.third ?: today.dayOfMonth
            )
        }

        when (isEditingStartDate.value) {
            true -> updateStartDate(validDate)
            false -> updateStartDate(uiState.value?.novelRatingModel?.currentEndDate ?: return)
            else -> {}
        }
    }

    fun clearCurrentDate() {
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.currentStartDate = null
        uiState.novelRatingModel.currentEndDate = null
        _uiState.value = uiState
    }

    fun cancelDateEdit() {
        val uiState = uiState.value ?: return
        if (uiState.novelRatingModel.currentStartDate != null) {
            uiState.novelRatingModel.currentStartDate = pastStartDate
        }
        if (uiState.novelRatingModel.currentEndDate != null) {
            uiState.novelRatingModel.currentEndDate = pastEndDate
        }
        _uiState.value = uiState
    }

    fun createNotNullDate() {
        val novelRatingModel = uiState.value?.novelRatingModel ?: return
        val nowDateFormatted = LocalDate.now().toFormattedDate()

        when (novelRatingModel.uiReadStatus) {
            ReadStatus.WATCHING -> novelRatingModel.currentStartDate =
                novelRatingModel.currentStartDate ?: nowDateFormatted

            ReadStatus.WATCHED -> {
                novelRatingModel.currentStartDate =
                    novelRatingModel.currentStartDate ?: nowDateFormatted
                novelRatingModel.currentEndDate =
                    novelRatingModel.currentEndDate ?: nowDateFormatted
            }

            ReadStatus.QUIT -> novelRatingModel.currentEndDate =
                novelRatingModel.currentEndDate ?: nowDateFormatted
        }
        updatePastDate()
        _uiState.value = uiState.value
    }
}