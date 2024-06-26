package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.novelRating.manager.RatingDateManager
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
import com.teamwss.websoso.ui.novelRating.model.RatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

class NovelRatingViewModel : ViewModel() {
    private val _maxDayValue = MutableLiveData<Int>()
    val maxDayValue: LiveData<Int> get() = _maxDayValue

    private val _uiState = MutableLiveData<NovelRatingUiState>()
    val uiState: LiveData<NovelRatingUiState> get() = _uiState

    private val _isEditingStartDate = MutableLiveData<Boolean>()
    val isEditingStartDate: LiveData<Boolean> get() = _isEditingStartDate

    private val ratingDateManager = RatingDateManager()

    fun updatePreviousDate() {
        _uiState.value?.let { uiState ->
            uiState.novelRatingModel.ratingDateModel.run {
                previousStartDate = currentStartDate
                previousEndDate = currentEndDate
            }
            _uiState.value = uiState
        }
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        _uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.updateReadStatus(uiState.novelRatingModel, readStatus)
            _uiState.value = uiState.copy(novelRatingModel = updatedModel)
            _isEditingStartDate.value = ratingDateManager.updateIsEditingStartDate(readStatus)
        }
    }

    fun toggleEditingStartDate(boolean: Boolean) {
        _isEditingStartDate.value = boolean
    }

    fun updateCurrentDate(date: Triple<Int, Int, Int>) {
        _uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.updateCurrentDate(
                    uiState.novelRatingModel,
                    date,
                    _isEditingStartDate.value!!,
                )
            _uiState.value =
                uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
            _maxDayValue.value =
                ratingDateManager.updateDayMaxValue(
                    uiState.novelRatingModel.ratingDateModel,
                    _isEditingStartDate.value!!,
                )
        }
    }

    fun clearCurrentDate() {
        _uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.clearCurrentDate(uiState.novelRatingModel.ratingDateModel)
            _uiState.value =
                uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
        }
    }

    fun cancelDateEdit() {
        _uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.cancelDateEdit(uiState.novelRatingModel.ratingDateModel)
            _uiState.value =
                uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
        }
    }

    fun getNotNullDate() {
        _uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.getNotNullDate(uiState.novelRatingModel)
            _uiState.value =
                uiState.copy(novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel))
        }
    }

    fun updateCurrentSelectedKeywords(
        keyword: RatingKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ) {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.ratingKeywordModel.categories, keyword, isSelected)
            val currentSelectedKeywords =
                updateSelectedKeywords(
                    uiState.ratingKeywordModel.currentSelectedKeywords,
                    keyword,
                    isSelected,
                )

            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            categories = updatedCategories,
                            currentSelectedKeywords = currentSelectedKeywords,
                        ),
                )
        }
    }

    fun updatePreviousSelectedKeywords(keyword: RatingKeywordModel.CategoryModel.KeywordModel) {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.ratingKeywordModel.categories, keyword, false)
            val previousSelectedKeywords =
                uiState.ratingKeywordModel.previousSelectedKeywords.filterNot { it.keywordId == keyword.keywordId }

            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            categories = updatedCategories,
                            previousSelectedKeywords = previousSelectedKeywords,
                        ),
                )
        }
    }

    private fun updateCategories(
        categories: List<RatingKeywordModel.CategoryModel>,
        keyword: RatingKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ): List<RatingKeywordModel.CategoryModel> {
        return categories.map { category ->
            val updatedKeywords =
                category.keywords.map { keywordInCategory ->
                    when (keywordInCategory.keywordId == keyword.keywordId) {
                        true -> keywordInCategory.copy(isSelected = isSelected)
                        false -> keywordInCategory
                    }
                }
            category.copy(keywords = updatedKeywords)
        }
    }

    private fun updateSelectedKeywords(
        currentSelectedKeywords: List<RatingKeywordModel.CategoryModel.KeywordModel>,
        keyword: RatingKeywordModel.CategoryModel.KeywordModel,
        isSelected: Boolean,
    ): List<RatingKeywordModel.CategoryModel.KeywordModel> {
        return currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }
    }

    fun initCurrentSelectedKeywords() {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.ratingKeywordModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword ->
                            keyword.copy(
                                isSelected =
                                    uiState.ratingKeywordModel.previousSelectedKeywords.any {
                                        it.keywordId == keyword.keywordId
                                    },
                            )
                        }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            categories = updatedCategories,
                            currentSelectedKeywords = uiState.ratingKeywordModel.previousSelectedKeywords,
                        ),
                )
        }
    }

    fun saveSelectedKeywords() {
        _uiState.value?.let { uiState ->
            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            previousSelectedKeywords = uiState.ratingKeywordModel.currentSelectedKeywords,
                        ),
                )
        }
    }

    fun clearEditingKeyword() {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.ratingKeywordModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword -> keyword.copy(isSelected = false) }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            categories = updatedCategories,
                            previousSelectedKeywords = emptyList(),
                            currentSelectedKeywords = emptyList(),
                        ),
                )
        }
    }

    fun cancelEditingKeyword() {
        _uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.ratingKeywordModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword ->
                            keyword.copy(
                                isSelected =
                                    uiState.ratingKeywordModel.previousSelectedKeywords.any {
                                        it.keywordId == keyword.keywordId
                                    },
                            )
                        }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    ratingKeywordModel =
                        uiState.ratingKeywordModel.copy(
                            categories = updatedCategories,
                            currentSelectedKeywords = uiState.ratingKeywordModel.previousSelectedKeywords,
                        ),
                )
        }
    }

    // 날릴예정
    fun getDummy() {
        _uiState.value =
            NovelRatingUiState(
                novelRatingModel =
                    NovelRatingModel(
                        userNovelId = 1,
                        novelTitle = "철혈검가 사냥개의 회귀",
                        userNovelRating = 4.0f,
                        readStatus = "WATCHED",
                        startDate = "2023-02-28",
                        endDate = "2024-05-11",
                    ),
                ratingKeywordModel =
                    RatingKeywordModel(
                        categories =
                            listOf(
                                RatingKeywordModel.CategoryModel(
                                    categoryName = "세계관",
                                    keywords =
                                        listOf(
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 1,
                                                keywordName = "이세계",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 2,
                                                keywordName = "현대",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 3,
                                                keywordName = "서양풍/중세시대",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 4,
                                                keywordName = "SF",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 5,
                                                keywordName = "동양풍/사극",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 6,
                                                keywordName = "학원/아카데미",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 7,
                                                keywordName = "실존역사",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 16,
                                                keywordName = "전투",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 17,
                                                keywordName = "로맨스",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 18,
                                                keywordName = "판타지",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 19,
                                                keywordName = "드라마",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 20,
                                                keywordName = "스릴러",
                                            ),
                                        ),
                                ),
                                RatingKeywordModel.CategoryModel(
                                    categoryName = "소재",
                                    keywords =
                                        listOf(
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 8,
                                                keywordName = "웹툰화",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 9,
                                                keywordName = "드라마화",
                                            ),
                                        ),
                                ),
                                RatingKeywordModel.CategoryModel(
                                    categoryName = "캐릭터",
                                    keywords =
                                        listOf(
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 10,
                                                keywordName = "영웅",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 11,
                                                keywordName = "악당/빌런",
                                            ),
                                        ),
                                ),
                                RatingKeywordModel.CategoryModel(
                                    categoryName = "관계",
                                    keywords =
                                        listOf(
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 12,
                                                keywordName = "친구",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 13,
                                                keywordName = "동료",
                                            ),
                                        ),
                                ),
                                RatingKeywordModel.CategoryModel(
                                    categoryName = "분위기",
                                    keywords =
                                        listOf(
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 14,
                                                keywordName = "뻔한",
                                            ),
                                            RatingKeywordModel.CategoryModel.KeywordModel(
                                                keywordId = 15,
                                                keywordName = "반전있는",
                                            ),
                                        ),
                                ),
                            ),
                    ),
            )
        dummyInit()
    }

    private fun dummyInit() {
        _uiState.value?.let { uiState ->
            _isEditingStartDate.value =
                ratingDateManager.updateIsEditingStartDate(uiState.novelRatingModel.uiReadStatus)
            _maxDayValue.value =
                ratingDateManager.updateDayMaxValue(
                    uiState.novelRatingModel.ratingDateModel,
                    _isEditingStartDate.value!!,
                )
        }
    }
}
