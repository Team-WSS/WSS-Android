package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.novelRating.manager.RatingDateManager
import com.teamwss.websoso.ui.novelRating.model.KeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

class NovelRatingViewModel : ViewModel() {
    private val _maxDayValue = MutableLiveData<Int>()
    val maxDayValue: LiveData<Int> get() = _maxDayValue

    private val _uiState = MutableLiveData<NovelRatingUiState>()
    val uiState: LiveData<NovelRatingUiState> get() = _uiState

    private val _isEditingStartDate = MutableLiveData<Boolean>()
    val isEditingStartDate: LiveData<Boolean> get() = _isEditingStartDate

    fun updatePastDate() {
        val uiState = uiState.value ?: return
        val ratingDateModel = uiState.novelRatingModel.ratingDateModel
        ratingDateModel.pastStartDate = ratingDateModel.currentStartDate
        ratingDateModel.pastEndDate = ratingDateModel.currentEndDate
        _uiState.value = uiState
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        RatingDateManager().updateReadStatus(uiState.value?.novelRatingModel ?: return, readStatus)
            .let { updatedModel ->
                _uiState.value = uiState.value?.copy(novelRatingModel = updatedModel)
                _isEditingStartDate.value = RatingDateManager().updateIsEditingStartDate(readStatus)
            }
    }

    fun toggleEditingStartDate(boolean: Boolean) {
        _isEditingStartDate.value = boolean
    }

    fun updateCurrentDate(date: Triple<Int, Int, Int>) {
        val uiState = uiState.value ?: return
        RatingDateManager().updateCurrentDate(
            uiState.novelRatingModel,
            date,
            isEditingStartDate.value!!
        )
            .let { updatedModel ->
                _uiState.value =
                    uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
                _maxDayValue.value = RatingDateManager().updateDayMaxValue(
                    uiState.novelRatingModel.ratingDateModel,
                    isEditingStartDate.value!!
                )
            }
    }

    fun clearCurrentDate() {
        val uiState = uiState.value ?: return
        RatingDateManager().clearCurrentDate(uiState.novelRatingModel.ratingDateModel)
            .let { updatedModel ->
                _uiState.value =
                    uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
            }
    }

    fun cancelDateEdit() {
        val uiState = uiState.value ?: return
        RatingDateManager().cancelDateEdit(uiState.novelRatingModel.ratingDateModel)
            .let { updatedModel ->
                _uiState.value =
                    uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
            }
    }

    fun createNotNullDate() {
        val uiState = uiState.value ?: return
        RatingDateManager().createNotNullDate(uiState.novelRatingModel)
            .let { updatedModel ->
                _uiState.value =
                    uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
            }
    }

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

    // 날릴예정
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
        dummyInit()
    }

    private fun dummyInit() {
        val uiState = uiState.value ?: return
        _isEditingStartDate.value =
            RatingDateManager().updateIsEditingStartDate(uiState.novelRatingModel.uiReadStatus)
        _maxDayValue.value = RatingDateManager().updateDayMaxValue(
            uiState.novelRatingModel.ratingDateModel,
            isEditingStartDate.value!!
        )
    }
}