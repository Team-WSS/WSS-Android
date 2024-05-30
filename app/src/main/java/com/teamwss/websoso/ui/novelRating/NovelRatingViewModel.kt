package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.novelRating.model.Month
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
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
        updateIsEditingStartDate()
        updatePastDate()
        updateDayMaxValue()
    }

    private fun updateIsEditingStartDate() {
        val uiState = uiState.value ?: return
        when (uiState.novelRatingModel.uiReadStatus) {
            ReadStatus.WATCHING -> _isEditingStartDate.value = true
            ReadStatus.WATCHED -> _isEditingStartDate.value = true
            ReadStatus.QUIT -> _isEditingStartDate.value = false
        }
    }

    fun updatePastDate() {
        val uiState = uiState.value ?: return
        pastStartDate = uiState.novelRatingModel.currentStartDate
        pastEndDate = uiState.novelRatingModel.currentEndDate
        _uiState.value = uiState
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
        updateIsEditingStartDate()
    }

    private fun handleWatchingState(uiState: NovelRatingUiState) {
        with(uiState.novelRatingModel) {
            currentStartDate = when (pastStartDate == null) {
                true -> LocalDate.now().toFormattedDate()
                false -> pastStartDate
            }
            pastEndDate = currentEndDate
            currentEndDate = null
        }
    }

    private fun handleWatchedState(uiState: NovelRatingUiState) {
        with(uiState.novelRatingModel) {
            currentStartDate = when (pastStartDate == null) {
                true -> LocalDate.now().toFormattedDate()
                false -> pastStartDate
            }
            currentEndDate = when (pastEndDate == null) {
                true -> LocalDate.now().toFormattedDate()
                false -> pastEndDate
            }
            checkValidityIsStartAfterEnd()
        }
    }

    private fun handleQuitState(uiState: NovelRatingUiState) {
        with(uiState.novelRatingModel) {
            currentEndDate = when (pastEndDate == null) {
                true -> LocalDate.now().toFormattedDate()
                false -> pastEndDate
            }
            pastStartDate = currentStartDate
            currentStartDate = null
        }
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

    private fun updateDayMaxValue() {
        _maxDayValue.value = when (isEditingStartDate.value) {
            true -> Month.getDays(
                uiState.value?.novelRatingModel?.currentStartDate?.first ?: LocalDate.now().year,
                uiState.value?.novelRatingModel?.currentStartDate?.second
                    ?: LocalDate.now().monthValue
            )

            false -> Month.getDays(
                uiState.value?.novelRatingModel?.currentEndDate?.first ?: LocalDate.now().year,
                uiState.value?.novelRatingModel?.currentEndDate?.second
                    ?: LocalDate.now().monthValue
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
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.currentStartDate?.let {
            if (it.toLocalDate().isAfter(LocalDate.now())) resetReadDateToToday()
        }
        uiState.novelRatingModel.currentEndDate?.let {
            if (it.toLocalDate().isAfter(LocalDate.now())) resetReadDateToToday()
        }
    }

    private fun Triple<Int, Int, Int>?.toLocalDate(): LocalDate {
        if (this == null) return LocalDate.now()
        return when (this.third > Month.getDays(this.first, this.second)) {
            true -> LocalDate.of(this.first, this.second, Month.getDays(this.first, this.second))
            false -> return LocalDate.of(this.first, this.second, this.third)
        }
    }

    private fun resetReadDateToToday() {
        when (isEditingStartDate.value) {
            true -> updateStartDate(LocalDate.now().toFormattedDate())
            false -> updateEndDate(LocalDate.now().toFormattedDate())
            else -> {}
        }
    }

    private fun checkValidityIsStartAfterEnd() {
        val uiState = uiState.value ?: return
        val startDate = uiState.novelRatingModel.currentStartDate.toLocalDate()
        val endDate = uiState.novelRatingModel.currentEndDate.toLocalDate()

        if (startDate.isAfter(endDate)) resetWatchedReadDate()
    }

    private fun resetWatchedReadDate() {
        when (isEditingStartDate.value) {
            true -> uiState.value?.novelRatingModel?.currentEndDate?.let { updateStartDate(it) }
            false -> uiState.value?.novelRatingModel?.currentStartDate?.let { updateEndDate(it) }
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
}