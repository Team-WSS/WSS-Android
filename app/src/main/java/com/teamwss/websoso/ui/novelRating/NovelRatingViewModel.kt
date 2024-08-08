package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.model.NovelRatingKeywordCategoryEntity
import com.teamwss.websoso.data.repository.UserNovelRepository
import com.teamwss.websoso.ui.mapper.toData
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelRating.model.CharmPoint
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordsModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel.Companion.toFormattedDate
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import com.teamwss.websoso.ui.novelRating.util.RatingDateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelRatingViewModel @Inject constructor(
    private val userNovelRepository: UserNovelRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<NovelRatingUiState>(NovelRatingUiState())
    val uiState: LiveData<NovelRatingUiState> get() = _uiState
    private val ratingDateManager = RatingDateManager()

    fun updateNovelRating(novelId: Long) {
        viewModelScope.launch {
            runCatching {
                _uiState.value = uiState.value?.copy(loading = true)
                userNovelRepository.fetchNovelRating(novelId)
            }.onSuccess { novelRatingEntity ->
                handleSuccessfulFetchNovelRating(novelRatingEntity)
            }.onFailure {
                _uiState.value =
                    uiState.value?.copy(
                        loading = false,
                        isFetchError = true,
                    )
            }
        }
    }

    private fun handleSuccessfulFetchNovelRating(novelRatingEntity: NovelRatingEntity) {
        val novelRatingModel = novelRatingEntity.toUi()
        val isEditingStartDate =
            ratingDateManager.updateIsEditingStartDate(novelRatingModel.uiReadStatus)
        val dayMaxValue =
            ratingDateManager.updateDayMaxValue(
                novelRatingModel.ratingDateModel,
                isEditingStartDate,
            )
        _uiState.value =
            uiState.value?.copy(
                novelRatingModel = novelRatingModel,
                keywordsModel = NovelRatingKeywordsModel(
                    currentSelectedKeywords = novelRatingModel.userKeywords,
                ),
                isEditingStartDate = isEditingStartDate,
                maxDayValue = dayMaxValue,
                loading = false,
            )
//        updateKeywordCategories()
    }

    /*
        // TODO: 명지 키워드 뷰 병합 이후 수정
        fun updateKeywordCategories(keyword: String = "") {
            viewModelScope.launch {
                runCatching {
                    fakeNovelRatingRepository.fetchNovelRatingKeywordCategories(keyword)
                }.onSuccess { categories ->
                    handleSuccessfulFetchKeywordCategories(categories)
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    */
    private fun handleSuccessfulFetchKeywordCategories(categories: List<NovelRatingKeywordCategoryEntity>) {
        val previousSelectedKeywords = uiState.value?.keywordsModel?.currentSelectedKeywords ?: emptyList()
        val updatedCategories = categories.map { it.toUi() }.map {
            it.copy(keywords = it.keywords.map { keyword ->
                keyword.copy(isSelected = previousSelectedKeywords.contains(keyword))
            })
        }
        _uiState.value = uiState.value?.copy(
            keywordsModel = NovelRatingKeywordsModel(
                categories = updatedCategories,
                currentSelectedKeywords = previousSelectedKeywords,
            ),
            loading = false,
        )
    }

    fun updatePreviousDate() {
        uiState.value?.let { currentState ->
            val updatedRatingDateModel =
                currentState.novelRatingModel.ratingDateModel.copy(
                    previousStartDate = currentState.novelRatingModel.ratingDateModel.currentStartDate,
                    previousEndDate = currentState.novelRatingModel.ratingDateModel.currentEndDate,
                )

            val updatedNovelRatingModel =
                currentState.novelRatingModel.copy(
                    ratingDateModel = updatedRatingDateModel,
                )

            _uiState.value = currentState.copy(novelRatingModel = updatedNovelRatingModel)
        }
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.updateReadStatus(uiState.novelRatingModel, readStatus)
            _uiState.value =
                uiState.copy(
                    novelRatingModel = updatedModel,
                    isEditingStartDate = ratingDateManager.updateIsEditingStartDate(readStatus),
                )
        }
    }

    fun toggleEditingStartDate(boolean: Boolean) {
        uiState.value?.let { uiState ->
            _uiState.value =
                uiState.copy(
                    isEditingStartDate = boolean,
                )
        }
    }

    fun updateCurrentDate(date: Triple<Int, Int, Int>) {
        uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.updateCurrentDate(
                    uiState.novelRatingModel,
                    date,
                    uiState.isEditingStartDate,
                )
            val maxDayValue =
                ratingDateManager.updateDayMaxValue(
                    updatedModel,
                    uiState.isEditingStartDate,
                )
            _uiState.value =
                uiState.copy(
                    novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
                    maxDayValue = maxDayValue,
                )
        }
    }

    fun clearCurrentDate() {
        uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.clearCurrentDate(
                    uiState.novelRatingModel.ratingDateModel,
                )
            _uiState.value =
                uiState.copy(
                    novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
                )
        }
    }

    fun cancelDateEdit() {
        uiState.value?.let { uiState ->
            val updatedModel =
                ratingDateManager.cancelDateEdit(
                    uiState.novelRatingModel.ratingDateModel,
                )
            _uiState.value =
                uiState.copy(
                    novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
                )
        }
    }

    fun updateNotNullDate() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.getNotNullDate(uiState.novelRatingModel)
            _uiState.value =
                uiState.copy(
                    novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
                )
        }
    }

    fun updateCharmPoints(charmPoint: CharmPoint) {
        uiState.value?.let { uiState ->
            val charmPoints = uiState.novelRatingModel.charmPoints.toMutableList()
            when (charmPoints.contains(charmPoint)) {
                true -> charmPoints.remove(charmPoint)
                false -> charmPoints.add(charmPoint)
            }
            val updatedNovelRatingModel =
                uiState.novelRatingModel.copy(charmPoints = charmPoints.toList())
            _uiState.value = uiState.copy(novelRatingModel = updatedNovelRatingModel)
        }
    }

    fun updateSelectedKeywords(keyword: NovelRatingKeywordModel, isSelected: Boolean) {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                keywordsModel = uiState.keywordsModel.copy(
                    categories = uiState.keywordsModel.updatedCategories(keyword.copy(isSelected = isSelected)),
                    currentSelectedKeywords = uiState.keywordsModel.currentSelectedKeywords.toMutableList().apply {
                        when (isSelected) {
                            true -> add(keyword)
                            false -> this.removeIf { it.keywordId == keyword.keywordId }
                        }
                    }.toList(),
                ),
            )
        }
    }

    fun saveSelectedKeywords() {
        uiState.value?.let { uiState ->
            val selectedKeywords = uiState.keywordsModel.currentSelectedKeywords
            val updatedRatingModel = uiState.novelRatingModel.copy(userKeywords = selectedKeywords)
            _uiState.value = uiState.copy(novelRatingModel = updatedRatingModel)
        }
    }

    fun clearEditingKeyword() {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(userKeywords = emptyList()),
                keywordsModel = uiState.keywordsModel.copy(
                    categories = uiState.keywordsModel.categories.map { category ->
                        category.copy(keywords = category.keywords.map { keyword ->
                            keyword.copy(isSelected = false)
                        })
                    },
                    currentSelectedKeywords = emptyList(),
                ),
            )
        }
    }

    fun cancelEditingKeyword() {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                keywordsModel = uiState.keywordsModel.copy(
                    currentSelectedKeywords = uiState.novelRatingModel.userKeywords,
                ),
            )
        }
    }

    fun updateNovelRating(novelId: Long, isAlreadyRated: Boolean) {
        viewModelScope.launch {
            runCatching {
                userNovelRepository.saveNovelRating(
                    NovelRatingEntity(
                        novelId = novelId,
                        readStatus = uiState.value?.novelRatingModel?.uiReadStatus?.name
                            ?: throw IllegalArgumentException("readStatus must not be null"),
                        startDate = uiState.value?.novelRatingModel?.ratingDateModel?.previousStartDate?.toFormattedDate(),
                        endDate = uiState.value?.novelRatingModel?.ratingDateModel?.previousEndDate?.toFormattedDate(),
                        userNovelRating = uiState.value?.novelRatingModel?.userNovelRating ?: 0.0f,
                        charmPoints = uiState.value?.novelRatingModel?.charmPoints?.map { it.value } ?: emptyList(),
                        userKeywords = uiState.value?.novelRatingModel?.userKeywords?.map { it.toData() } ?: emptyList(),
                    ),
                    isAlreadyRated
                )
            }.onSuccess {
                _uiState.value = uiState.value?.copy(isSaveSuccess = true)
            }.onFailure {
                _uiState.value = uiState.value?.copy(isSaveError = true)
            }
        }
    }
}
