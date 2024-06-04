package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.novelRating.model.DatePickerType
import com.teamwss.websoso.ui.novelRating.model.KeywordModel
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

    // 네트워크 함수 (추가 예정)
    fun getDummy() {
        _uiState.value = NovelRatingUiState(
            novelRatingModel = NovelRatingModel(
                userNovelId = 1,
                novelTitle = "철혈검가 사냥개의 회귀",
                userNovelRating = 4.0f,
                readStatus = "WATCHED",
                startDate = "2023-02-28",
                endDate = "2024-05-11"
            ),
            keywordModel = KeywordModel(
                categories = listOf(
                    KeywordModel.Category(
                        categoryName = "세계관",
                        keywords = listOf(
                            KeywordModel.Category.Keyword(keywordId = 1, keywordName = "이세계"),
                            KeywordModel.Category.Keyword(keywordId = 2, keywordName = "현대"),
                            KeywordModel.Category.Keyword(keywordId = 3, keywordName = "서양풍/중세시대"),
                            KeywordModel.Category.Keyword(keywordId = 4, keywordName = "SF"),
                            KeywordModel.Category.Keyword(keywordId = 5, keywordName = "동양풍/사극"),
                            KeywordModel.Category.Keyword(keywordId = 6, keywordName = "학원/아카데미"),
                            KeywordModel.Category.Keyword(keywordId = 7, keywordName = "실존역사"),
                            KeywordModel.Category.Keyword(keywordId = 16, keywordName = "전투"),
                            KeywordModel.Category.Keyword(keywordId = 17, keywordName = "로맨스"),
                            KeywordModel.Category.Keyword(keywordId = 18, keywordName = "판타지"),
                            KeywordModel.Category.Keyword(keywordId = 19, keywordName = "드라마"),
                            KeywordModel.Category.Keyword(keywordId = 20, keywordName = "스릴러"),
                        )
                    ),
                    KeywordModel.Category(
                        categoryName = "소재",
                        keywords = listOf(
                            KeywordModel.Category.Keyword(keywordId = 8, keywordName = "웹툰화"),
                            KeywordModel.Category.Keyword(keywordId = 9, keywordName = "드라마화")
                        )
                    ),
                    KeywordModel.Category(
                        categoryName = "캐릭터",
                        keywords = listOf(
                            KeywordModel.Category.Keyword(keywordId = 10, keywordName = "영웅"),
                            KeywordModel.Category.Keyword(keywordId = 11, keywordName = "악당/빌런")
                        )
                    ),
                    KeywordModel.Category(
                        categoryName = "관계",
                        keywords = listOf(
                            KeywordModel.Category.Keyword(keywordId = 12, keywordName = "친구"),
                            KeywordModel.Category.Keyword(keywordId = 13, keywordName = "동료")
                        )
                    ),
                    KeywordModel.Category(
                        categoryName = "분위기",
                        keywords = listOf(
                            KeywordModel.Category.Keyword(keywordId = 14, keywordName = "뻔한"),
                            KeywordModel.Category.Keyword(keywordId = 15, keywordName = "반전있는")
                        )
                    )
                )
            ),
        )
    }

    // 날짜 관련 함수
    private fun updateIsEditingStartDate(readStatus: ReadStatus) {
        when (readStatus) {
            ReadStatus.WATCHING -> _isEditingStartDate.value = true
            ReadStatus.WATCHED -> _isEditingStartDate.value = true
            ReadStatus.QUIT -> _isEditingStartDate.value = false
        }
    }

    fun updatePastDate() {
        val uiState = uiState.value ?: return
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        ratingDateModel.pastStartDate = ratingDateModel.currentStartDate
        ratingDateModel.pastEndDate = ratingDateModel.currentEndDate
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
        updateIsEditingStartDate(readStatus)
    }

    private fun handleWatchingState(uiState: NovelRatingUiState) {
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate == null && ratingDateModel.currentEndDate == null) return
        ratingDateModel.currentStartDate = when (ratingDateModel.pastStartDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> ratingDateModel.pastStartDate
        }
        ratingDateModel.pastEndDate = ratingDateModel.currentEndDate
        ratingDateModel.currentEndDate = null
    }

    private fun handleWatchedState(uiState: NovelRatingUiState) {
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate == null && ratingDateModel.currentEndDate == null) return
        ratingDateModel.currentStartDate = when (ratingDateModel.pastStartDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> ratingDateModel.pastStartDate
        }
        ratingDateModel.currentEndDate = when (ratingDateModel.pastEndDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> ratingDateModel.pastEndDate
        }
        checkValidityIsStartAfterEnd()
    }

    private fun handleQuitState(uiState: NovelRatingUiState) {
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate == null && ratingDateModel.currentEndDate == null) return
        ratingDateModel.currentEndDate = when (ratingDateModel.pastEndDate == null) {
            true -> LocalDate.now().toFormattedDate()
            false -> ratingDateModel.pastEndDate
        }
        ratingDateModel.pastStartDate = ratingDateModel.currentStartDate
        ratingDateModel.currentStartDate = null
    }

    private fun LocalDate.toFormattedDate(): Triple<Int, Int, Int> {
        return Triple(this.year, this.monthValue, this.dayOfMonth)
    }

    private fun updateStartDate(date: Triple<Int, Int, Int>) {
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.ratingDateModel.currentStartDate = date
        _uiState.value = uiState
        updateDayMaxValue()
        updateDateValidity()
    }

    private fun updateEndDate(date: Triple<Int, Int, Int>) {
        val uiState = uiState.value ?: return
        uiState.novelRatingModel.ratingDateModel.currentEndDate = date
        _uiState.value = uiState
        updateDayMaxValue()
        updateDateValidity()
    }

    fun updateDayMaxValue() {
        val ratingDateModel = uiState.value?.novelRatingModel?.ratingDateModel ?: return
        _maxDayValue.value = when (isEditingStartDate.value) {
            true -> Month.getDays(
                ratingDateModel.currentStartDate?.first ?: LocalDate.now().year,
                ratingDateModel.currentStartDate?.second ?: LocalDate.now().monthValue
            )

            false -> Month.getDays(
                ratingDateModel.currentEndDate?.first ?: LocalDate.now().year,
                ratingDateModel.currentEndDate?.second ?: LocalDate.now().monthValue
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
        val startLocalDate =
            uiState.value?.novelRatingModel?.ratingDateModel?.currentStartDate.toLocalDate()
        val endLocalDate =
            uiState.value?.novelRatingModel?.ratingDateModel?.currentEndDate.toLocalDate()

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
        val ratingDateModel = uiState.value?.novelRatingModel?.ratingDateModel ?: return
        val today = LocalDate.now()

        val (year, month, day) = when (datePickerType) {
            DatePickerType.YEAR -> Triple(today.year, null, null)
            DatePickerType.MONTH -> Triple(null, today.monthValue, null)
            DatePickerType.DAY -> Triple(null, null, today.dayOfMonth)
        }

        val newDate = Triple(
            year
                ?: (if (isEditingStartDate) ratingDateModel.currentStartDate else ratingDateModel.currentEndDate)?.first
                ?: today.year,
            month
                ?: (if (isEditingStartDate) ratingDateModel.currentStartDate else ratingDateModel.currentEndDate)?.second
                ?: today.monthValue,
            day
                ?: (if (isEditingStartDate) ratingDateModel.currentStartDate else ratingDateModel.currentEndDate)?.third
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
        val ratingDateModel = uiState.value?.novelRatingModel?.ratingDateModel ?: return
        val startLocalDate = ratingDateModel.currentStartDate.toLocalDate()
        val endLocalDate = ratingDateModel.currentEndDate.toLocalDate()
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
        val ratingDateModel = uiState.value?.novelRatingModel?.ratingDateModel ?: return
        val today = LocalDate.now()
        val validDate = when (datePickerType) {
            DatePickerType.YEAR -> Triple(
                ratingDateModel.currentEndDate?.first ?: today.year,
                ratingDateModel.currentStartDate?.second ?: today.monthValue,
                ratingDateModel.currentStartDate?.third ?: today.dayOfMonth
            )

            DatePickerType.MONTH -> Triple(
                ratingDateModel.currentStartDate?.first ?: today.year,
                ratingDateModel.currentEndDate?.second ?: today.monthValue,
                ratingDateModel.currentStartDate?.third ?: today.dayOfMonth
            )

            DatePickerType.DAY -> Triple(
                ratingDateModel.currentStartDate?.first ?: today.year,
                ratingDateModel.currentStartDate?.second ?: today.monthValue,
                ratingDateModel.currentEndDate?.third ?: today.dayOfMonth
            )
        }

        when (isEditingStartDate.value) {
            true -> updateStartDate(validDate)
            false -> updateStartDate(
                uiState.value?.novelRatingModel?.ratingDateModel?.currentEndDate ?: return
            )

            else -> {}
        }
    }

    fun clearCurrentDate() {
        val uiState = uiState.value ?: return
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        ratingDateModel.currentStartDate = null
        ratingDateModel.currentEndDate = null
        _uiState.value = uiState
    }

    fun cancelDateEdit() {
        val uiState = uiState.value ?: return
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        if (ratingDateModel.currentStartDate != null) {
            ratingDateModel.currentStartDate = ratingDateModel.pastStartDate
        }
        if (ratingDateModel.currentEndDate != null) {
            ratingDateModel.currentEndDate = ratingDateModel.pastEndDate
        }
        _uiState.value = uiState
    }

    fun createNotNullDate() {
        val novelRatingModel = uiState.value?.novelRatingModel ?: return
        val ratingDateModel = novelRatingModel.ratingDateModel
        val nowDateFormatted = LocalDate.now().toFormattedDate()

        when (novelRatingModel.uiReadStatus) {
            ReadStatus.WATCHING -> ratingDateModel.currentStartDate =
                ratingDateModel.currentStartDate ?: nowDateFormatted

            ReadStatus.WATCHED -> {
                ratingDateModel.currentStartDate =
                    ratingDateModel.currentStartDate ?: nowDateFormatted
                ratingDateModel.currentEndDate =
                    ratingDateModel.currentEndDate ?: nowDateFormatted
            }

            ReadStatus.QUIT -> ratingDateModel.currentEndDate =
                ratingDateModel.currentEndDate ?: nowDateFormatted
        }
        updatePastDate()
        _uiState.value = uiState.value
    }

    // 키워드 관련 함수
    fun updateCurrentSelectedKeywords(keyword: KeywordModel.Category.Keyword, isSelected: Boolean) {
        val uiState = uiState.value ?: return

        val updatedCategories =
            updateCategories(uiState.keywordModel.categories, keyword, isSelected)
        val currentSelectedKeywords = updateSelectedKeywords(
            uiState.keywordModel.currentSelectedKeywords,
            keyword,
            isSelected
        )

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                categories = updatedCategories,
                currentSelectedKeywords = currentSelectedKeywords
            )
        )
    }

    fun updatePastSelectedKeywords(keyword: KeywordModel.Category.Keyword) {
        val uiState = uiState.value ?: return

        val updatedCategories = updateCategories(uiState.keywordModel.categories, keyword, false)
        val pastSelectedKeywords =
            uiState.keywordModel.pastSelectedKeywords.filterNot { it.keywordId == keyword.keywordId }

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                categories = updatedCategories,
                pastSelectedKeywords = pastSelectedKeywords
            )
        )
    }

    private fun updateCategories(
        categories: List<KeywordModel.Category>,
        keyword: KeywordModel.Category.Keyword,
        isSelected: Boolean
    ): List<KeywordModel.Category> {
        return categories.map { category ->
            val updatedKeywords = category.keywords.map { keywordInCategory ->
                when (keywordInCategory.keywordId == keyword.keywordId) {
                    true -> keywordInCategory.copy(isSelected = isSelected)
                    false -> keywordInCategory
                }
            }
            category.copy(keywords = updatedKeywords)
        }
    }

    private fun updateSelectedKeywords(
        currentSelectedKeywords: List<KeywordModel.Category.Keyword>,
        keyword: KeywordModel.Category.Keyword,
        isSelected: Boolean
    ): List<KeywordModel.Category.Keyword> {
        return currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }
    }

    fun initCurrentSelectedKeywords() {
        val uiState = uiState.value ?: return

        val updatedCategories = uiState.keywordModel.categories.map { category ->
            val updatedKeywords = category.keywords.map { keyword ->
                keyword.copy(isSelected = uiState.keywordModel.pastSelectedKeywords.any { it.keywordId == keyword.keywordId })
            }
            category.copy(keywords = updatedKeywords)
        }

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                categories = updatedCategories,
                currentSelectedKeywords = uiState.keywordModel.pastSelectedKeywords
            )
        )
    }

    fun saveSelectedKeywords() {
        val uiState = uiState.value ?: return

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                pastSelectedKeywords = uiState.keywordModel.currentSelectedKeywords
            )
        )
    }

    fun clearKeywordEdit() {
        val uiState = uiState.value ?: return

        val updatedCategories = uiState.keywordModel.categories.map { category ->
            val updatedKeywords = category.keywords.map { keyword ->
                keyword.copy(isSelected = false)
            }
            category.copy(keywords = updatedKeywords)
        }

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                categories = updatedCategories,
                pastSelectedKeywords = emptyList(),
                currentSelectedKeywords = emptyList()
            )
        )
    }

    fun cancelKeywordEdit() {
        val uiState = uiState.value ?: return

        val updatedCategories = uiState.keywordModel.categories.map { category ->
            val updatedKeywords = category.keywords.map { keyword ->
                keyword.copy(isSelected = uiState.keywordModel.pastSelectedKeywords.any { it.keywordId == keyword.keywordId })
            }
            category.copy(keywords = updatedKeywords)
        }

        _uiState.value = uiState.copy(
            keywordModel = uiState.keywordModel.copy(
                categories = updatedCategories,
                currentSelectedKeywords = uiState.keywordModel.pastSelectedKeywords
            )
        )
    }
}