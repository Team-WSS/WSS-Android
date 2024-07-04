package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.repository.FakeNovelRatingRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelRating.manager.RatingDateManager
import com.teamwss.websoso.ui.novelRating.model.NovelRatingCategoryModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelRatingViewModel @Inject constructor(
    private val fakeNovelRatingRepository: FakeNovelRatingRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<NovelRatingUiState>(NovelRatingUiState())
    val uiState: LiveData<NovelRatingUiState> get() = _uiState

    private val ratingDateManager = RatingDateManager()

    fun updateNovelRating(userNovelId: Long) {
        viewModelScope.launch {
            runCatching {
                fakeNovelRatingRepository.fetchNovelRating(userNovelId)
            }.onSuccess { novelRatingEntity ->
                handleSuccessfulFetch(novelRatingEntity)
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    private fun handleSuccessfulFetch(novelRatingEntity: NovelRatingEntity) {
        val novelRatingModel = novelRatingEntity.toUi()
        val isEditingStartDate =
            ratingDateManager.updateIsEditingStartDate(novelRatingModel.uiReadStatus)
        val dayMaxValue = ratingDateManager.updateDayMaxValue(
            novelRatingModel.ratingDateModel,
            isEditingStartDate,
        )
        _uiState.value = _uiState.value?.copy(
            novelRatingModel = novelRatingModel,
            isEditingStartDate = isEditingStartDate,
            maxDayValue = dayMaxValue,
            loading = false,
        )
    }

    fun updatePreviousDate() {
        uiState.value?.let { uiState ->
            uiState.novelRatingModel.ratingDateModel.run {
                previousStartDate = currentStartDate
                previousEndDate = currentEndDate
            }
            _uiState.value = uiState
        }
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.updateReadStatus(uiState.novelRatingModel, readStatus)
            _uiState.value = uiState.copy(
                novelRatingModel = updatedModel,
                isEditingStartDate = ratingDateManager.updateIsEditingStartDate(readStatus),
            )
        }
    }

    fun toggleEditingStartDate(boolean: Boolean) {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                isEditingStartDate = boolean,
            )
        }
    }

    fun updateCurrentDate(date: Triple<Int, Int, Int>) {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.updateCurrentDate(
                uiState.novelRatingModel,
                date,
                uiState.isEditingStartDate,
            )
            val maxDayValue = ratingDateManager.updateDayMaxValue(
                uiState.novelRatingModel.ratingDateModel,
                uiState.isEditingStartDate,
            )
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
                maxDayValue = maxDayValue,
            )
        }
    }

    fun clearCurrentDate() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.clearCurrentDate(
                uiState.novelRatingModel.ratingDateModel
            )
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel)
            )
        }
    }

    fun cancelDateEdit() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.cancelDateEdit(
                uiState.novelRatingModel.ratingDateModel
            )
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel)
            )
        }
    }

    fun updateNotNullDate() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.getNotNullDate(uiState.novelRatingModel)
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel)
            )
        }
    }

    fun updateCurrentSelectedKeywords(
        keyword: NovelRatingKeywordModel,
        isSelected: Boolean,
    ) {
        uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.novelRatingKeywordsModel.categories, keyword, isSelected)
            val currentSelectedKeywords =
                updateSelectedKeywords(
                    uiState.novelRatingKeywordsModel.currentSelectedKeywords,
                    keyword,
                    isSelected,
                )

            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        categories = updatedCategories,
                        currentSelectedKeywords = currentSelectedKeywords,
                    ),
                )
        }
    }

    fun updatePreviousSelectedKeywords(keyword: NovelRatingKeywordModel) {
        uiState.value?.let { uiState ->
            val updatedCategories =
                updateCategories(uiState.novelRatingKeywordsModel.categories, keyword, false)
            val previousSelectedKeywords =
                uiState.novelRatingKeywordsModel.previousSelectedKeywords.filterNot { it.keywordId == keyword.keywordId }

            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        categories = updatedCategories,
                        previousSelectedKeywords = previousSelectedKeywords,
                    ),
                )
        }
    }

    private fun updateCategories(
        categories: List<NovelRatingCategoryModel>,
        keyword: NovelRatingKeywordModel,
        isSelected: Boolean,
    ): List<NovelRatingCategoryModel> {
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
        currentSelectedKeywords: List<NovelRatingKeywordModel>,
        keyword: NovelRatingKeywordModel,
        isSelected: Boolean,
    ): List<NovelRatingKeywordModel> {
        return currentSelectedKeywords.toMutableList().apply {
            when (isSelected) {
                true -> add(keyword)
                false -> removeIf { it.keywordId == keyword.keywordId }
            }
        }
    }

    fun initCurrentSelectedKeywords() {
        uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.novelRatingKeywordsModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword ->
                            keyword.copy(
                                isSelected =
                                uiState.novelRatingKeywordsModel.previousSelectedKeywords.any {
                                    it.keywordId == keyword.keywordId
                                },
                            )
                        }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        categories = updatedCategories,
                        currentSelectedKeywords = uiState.novelRatingKeywordsModel.previousSelectedKeywords,
                    ),
                )
        }
    }

    fun saveSelectedKeywords() {
        uiState.value?.let { uiState ->
            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        previousSelectedKeywords = uiState.novelRatingKeywordsModel.currentSelectedKeywords,
                    ),
                )
        }
    }

    fun clearEditingKeyword() {
        uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.novelRatingKeywordsModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword -> keyword.copy(isSelected = false) }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        categories = updatedCategories,
                        previousSelectedKeywords = emptyList(),
                        currentSelectedKeywords = emptyList(),
                    ),
                )
        }
    }

    fun cancelEditingKeyword() {
        uiState.value?.let { uiState ->
            val updatedCategories =
                uiState.novelRatingKeywordsModel.categories.map { category ->
                    val updatedKeywords =
                        category.keywords.map { keyword ->
                            keyword.copy(
                                isSelected =
                                uiState.novelRatingKeywordsModel.previousSelectedKeywords.any {
                                    it.keywordId == keyword.keywordId
                                },
                            )
                        }
                    category.copy(keywords = updatedKeywords)
                }

            _uiState.value =
                uiState.copy(
                    novelRatingKeywordsModel =
                    uiState.novelRatingKeywordsModel.copy(
                        categories = updatedCategories,
                        currentSelectedKeywords = uiState.novelRatingKeywordsModel.previousSelectedKeywords,
                    ),
                )
        }
    }
}
