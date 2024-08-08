package com.teamwss.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.NovelRatingEntity
import com.teamwss.websoso.data.repository.KeywordRepository
import com.teamwss.websoso.data.repository.UserNovelRepository
import com.teamwss.websoso.ui.common.model.KeywordsModel
import com.teamwss.websoso.ui.mapper.toData
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelRating.model.CharmPoint
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
    private val keywordRepository: KeywordRepository,
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
        val isEditingStartDate = ratingDateManager.updateIsEditingStartDate(novelRatingModel.uiReadStatus)
        val dayMaxValue = ratingDateManager.updateDayMaxValue(
            novelRatingModel.ratingDateModel,
            isEditingStartDate,
        )
        _uiState.value = uiState.value?.copy(
            novelRatingModel = novelRatingModel,
            keywordsModel = NovelRatingKeywordsModel(
                currentSelectedKeywords = novelRatingModel.userKeywords,
            ),
            isEditingStartDate = isEditingStartDate,
            isAlreadyRated = novelRatingEntity.readStatus != null,
            maxDayValue = dayMaxValue,
            loading = false,
        )
        updateKeywordCategories()
    }

    fun updateKeywordCategories(keyword: String? = null) {
        viewModelScope.launch {
            runCatching {
                if (keyword == null && uiState.value?.keywordsModel?.categories?.isNotEmpty() == true) return@launch
                keywordRepository.fetchKeywords(keyword)
            }.onSuccess { categories ->
                handleSuccessfulFetchKeywordCategories(keyword, categories.categories.map { it.toUi() })
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    isFetchError = true,
                )
            }
        }
    }

    private fun handleSuccessfulFetchKeywordCategories(keyword: String?, categories: List<KeywordsModel.CategoryModel>) {
        val selectedKeywords = uiState.value?.keywordsModel?.currentSelectedKeywords ?: emptyList()
        val updatedCategories = categories.map { it }.map {
            it.copy(keywords = it.keywords.map { keyword ->
                keyword.copy(isSelected = selectedKeywords.contains(keyword))
            })
        }
        when (keyword == null) {
            true -> updateDefaultKeywords(updatedCategories, selectedKeywords)

            false -> updateSearchResultKeywords(updatedCategories)
        }
    }

    private fun updateSearchResultKeywords(updatedCategories: List<KeywordsModel.CategoryModel>) {
        _uiState.value = uiState.value?.let { uiState ->
            uiState.copy(
                keywordsModel = uiState.keywordsModel.copy(
                    searchResultKeywords = updatedCategories.flatMap { it.keywords },
                )
            )
        }
    }

    private fun updateDefaultKeywords(
        updatedCategories: List<KeywordsModel.CategoryModel>,
        selectedKeywords: List<KeywordsModel.CategoryModel.KeywordModel>,
    ) {
        _uiState.value = uiState.value?.copy(
            keywordsModel = NovelRatingKeywordsModel(
                categories = updatedCategories,
                currentSelectedKeywords = selectedKeywords,
            ),
            loading = false,
        )
    }

    fun updatePreviousDate() {
        uiState.value?.let { currentState ->
            val updatedRatingDateModel = currentState.novelRatingModel.ratingDateModel.copy(
                previousStartDate = currentState.novelRatingModel.ratingDateModel.currentStartDate,
                previousEndDate = currentState.novelRatingModel.ratingDateModel.currentEndDate,
            )

            val updatedNovelRatingModel = currentState.novelRatingModel.copy(
                ratingDateModel = updatedRatingDateModel,
            )

            _uiState.value = currentState.copy(novelRatingModel = updatedNovelRatingModel)
        }
    }

    fun updateReadStatus(readStatus: ReadStatus) {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.updateReadStatus(uiState.novelRatingModel, readStatus)
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
                updatedModel,
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
                uiState.novelRatingModel.ratingDateModel,
            )
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
            )
        }
    }

    fun cancelDateEdit() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.cancelDateEdit(
                uiState.novelRatingModel.ratingDateModel,
            )
            _uiState.value = uiState.copy(
                novelRatingModel = uiState.novelRatingModel.copy(ratingDateModel = updatedModel),
            )
        }
    }

    fun updateNotNullDate() {
        uiState.value?.let { uiState ->
            val updatedModel = ratingDateManager.getNotNullDate(uiState.novelRatingModel)
            _uiState.value = uiState.copy(
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
            val updatedNovelRatingModel = uiState.novelRatingModel.copy(charmPoints = charmPoints.toList())
            _uiState.value = uiState.copy(novelRatingModel = updatedNovelRatingModel)
        }
    }

    fun updateSelectedKeywords(keyword: KeywordsModel.CategoryModel.KeywordModel, isSelected: Boolean) {
        uiState.value?.let { uiState ->
            _uiState.value = uiState.copy(
                keywordsModel = uiState.keywordsModel.updateSelectedKeywords(keyword, isSelected)
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

    fun updateUserNovelRating(novelId: Long, novelRating: Float) {
        viewModelScope.launch {
            runCatching {
                userNovelRepository.saveNovelRating(
                    novelRatingEntity = NovelRatingEntity(
                        novelId = novelId,
                        readStatus = uiState.value?.novelRatingModel?.uiReadStatus?.name
                            ?: throw IllegalArgumentException("readStatus must not be null"),
                        startDate = uiState.value?.novelRatingModel?.ratingDateModel?.currentStartDate?.toFormattedDate(),
                        endDate = uiState.value?.novelRatingModel?.ratingDateModel?.currentEndDate?.toFormattedDate(),
                        userNovelRating = novelRating,
                        charmPoints = uiState.value?.novelRatingModel?.charmPoints?.map { it.value } ?: emptyList(),
                        userKeywords = uiState.value?.novelRatingModel?.userKeywords?.map { it.toData() } ?: emptyList(),
                    ),
                    isAlreadyRated = uiState.value?.isAlreadyRated ?: false,
                )
            }.onSuccess {
                _uiState.value = uiState.value?.copy(isSaveSuccess = true)
            }.onFailure {
                _uiState.value = uiState.value?.copy(isSaveError = true)
            }
        }
    }
}
