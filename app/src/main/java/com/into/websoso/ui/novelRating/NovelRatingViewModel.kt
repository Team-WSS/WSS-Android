package com.into.websoso.ui.novelRating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.data.model.NovelRatingEntity
import com.into.websoso.data.repository.KeywordRepository
import com.into.websoso.data.repository.UserNovelRepository
import com.into.websoso.ui.main.feed.model.FeedModel
import com.into.websoso.ui.mapper.toData
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.novelDetail.model.NovelDetailModel
import com.into.websoso.ui.novelRating.model.CharmPoint
import com.into.websoso.ui.novelRating.model.NovelRatingKeywordsModel
import com.into.websoso.ui.novelRating.model.NovelRatingUiState
import com.into.websoso.ui.novelRating.model.RatingDateModel.Companion.toFormattedDate
import com.into.websoso.ui.novelRating.model.ReadStatus
import com.into.websoso.ui.novelRating.util.RatingDateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelRatingViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val userNovelRepository: UserNovelRepository,
        private val keywordRepository: KeywordRepository,
    ) : ViewModel() {
        private val novel: NovelDetailModel? = savedStateHandle["NOVEL"]
        private val feeds: List<FeedModel>? = savedStateHandle["FEEDS"]
        private val readStatus: ReadStatus? = savedStateHandle["READ_STATUS"]
        private val isInterested: Boolean? = savedStateHandle["IS_INTEREST"]
        private val _uiState = MutableLiveData(NovelRatingUiState())
        val uiState: LiveData<NovelRatingUiState> get() = _uiState
        private val ratingDateManager = RatingDateManager()

        fun updateNovelRating(isInterest: Boolean) {
            viewModelScope.launch {
                runCatching {
                    _uiState.value = uiState.value?.copy(loading = true)
                    userNovelRepository.fetchNovelRating(novel?.novel?.novelId ?: 0)
                }.onSuccess { novelRatingEntity ->
                    handleSuccessfulFetchNovelRating(novelRatingEntity, isInterest)
                }.onFailure {
                    _uiState.value =
                        uiState.value?.copy(
                            loading = false,
                            isFetchError = true,
                        )
                }
            }
        }

        private fun handleSuccessfulFetchNovelRating(
            novelRatingEntity: NovelRatingEntity,
            isInterest: Boolean,
        ) {
            val novelRatingModel = novelRatingEntity.toUi()
            val isEditingStartDate =
                ratingDateManager.updateIsEditingStartDate(novelRatingModel.uiReadStatus)
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
                isAlreadyRated = novelRatingEntity.readStatus != null || isInterest,
                maxDayValue = dayMaxValue,
                loading = false,
            )
            updateKeywordCategories()
        }

        fun updateKeywordCategories(keyword: String? = null) {
            viewModelScope.launch {
                runCatching {
                    if (keyword == null &&
                        uiState.value
                            ?.keywordsModel
                            ?.categories
                            ?.isNotEmpty() == true
                    ) {
                        return@launch
                    }
                    keywordRepository.fetchKeywords(keyword)
                }.onSuccess { categories ->
                    handleSuccessfulFetchKeywordCategories(
                        keyword,
                        categories.categories.map { it.toUi() },
                    )
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        isFetchError = true,
                    )
                }
            }
        }

        private fun handleSuccessfulFetchKeywordCategories(
            keyword: String?,
            categories: List<CategoryModel>,
        ) {
            val selectedKeywords = uiState.value?.keywordsModel?.currentSelectedKeywords ?: emptySet()
            val updatedCategories = categories.map { it }.map {
                it.copy(
                    keywords = it.keywords.map { keyword ->
                        keyword.copy(isSelected = selectedKeywords.contains(keyword))
                    },
                )
            }
            when (keyword == null) {
                true -> updateDefaultKeywords(updatedCategories, selectedKeywords)

                false -> updateSearchResultKeywords(updatedCategories)
            }
        }

        private fun updateSearchResultKeywords(updatedCategories: List<CategoryModel>) {
            _uiState.value = uiState.value?.let { uiState ->
                uiState.copy(
                    keywordsModel = uiState.keywordsModel.copy(
                        searchResultKeywords = updatedCategories.flatMap { it.keywords },
                        isInitialSearchKeyword = false,
                        isSearchResultKeywordsEmpty = updatedCategories
                            .flatMap { it.keywords }
                            .isEmpty(),
                    ),
                )
            }
        }

        private fun updateDefaultKeywords(
            updatedCategories: List<CategoryModel>,
            selectedKeywords: Set<KeywordModel>,
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

        fun updateReadStatus() {
            if (readStatus == ReadStatus.NONE || readStatus == null) return
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
                val updatedNovelRatingModel =
                    uiState.novelRatingModel.copy(
                        charmPoints = charmPoints.toList(),
                        isCharmPointExceed = charmPoints.size > MAX_CHARM_POINT_COUNT,
                    )
                _uiState.value = uiState.copy(novelRatingModel = updatedNovelRatingModel)
            }
        }

        fun updateSelectedKeywords(
            keyword: KeywordModel,
            isSelected: Boolean,
        ) {
            uiState.value?.let { uiState ->
                _uiState.value = uiState.copy(
                    keywordsModel = uiState.keywordsModel.updateSelectedKeywords(keyword, isSelected),
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
                    novelRatingModel = uiState.novelRatingModel.copy(userKeywords = setOf()),
                    keywordsModel = uiState.keywordsModel.copy(
                        categories = uiState.keywordsModel.categories.map { category ->
                            category.copy(
                                keywords = category.keywords.map { keyword ->
                                    keyword.copy(isSelected = false)
                                },
                            )
                        },
                        currentSelectedKeywords = setOf(),
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

        fun updateUserNovelRating(novelRating: Float) {
            viewModelScope.launch {
                val ratingModel = uiState.value?.novelRatingModel
                runCatching {
                    userNovelRepository.saveNovelRating(
                        isInterested = isInterested,
                        novelRatingEntity = NovelRatingEntity(
                            userNovelId = novel?.userNovel?.userNovelId,
                            novelId = novel?.novel?.novelId ?: 0,
                            userNovelRating = novel?.userRating?.novelRating ?: 0.0f,
                            novelRating = novelRating,
                            novelTitle = novel?.novel?.novelTitle,
                            novelImage = novel?.novel?.novelImage,
                            startDate = ratingModel
                                ?.ratingDateModel
                                ?.currentStartDate
                                ?.toFormattedDate(),
                            endDate = ratingModel
                                ?.ratingDateModel
                                ?.currentEndDate
                                ?.toFormattedDate(),
                            charmPoints = ratingModel
                                ?.charmPoints
                                ?.map { it.value }
                                ?: emptyList(),
                            userKeywords = ratingModel
                                ?.userKeywords
                                ?.map { it.toData() }
                                ?: emptyList(),
                            readStatus = ratingModel?.uiReadStatus?.name,
                        ),
                        isAlreadyRated = uiState.value?.isAlreadyRated ?: false,
                        feeds = feeds?.map { it.content } ?: emptyList(),
                    )
                }.onSuccess {
                    _uiState.value = uiState.value?.copy(isSaveSuccess = true)
                }.onFailure {
                    _uiState.value = uiState.value?.copy(isSaveError = true)
                }
            }
        }

        fun updateRatingValue(rating: Float) {
            uiState.value?.let { uiState ->
                _uiState.value = uiState.copy(
                    novelRatingModel = uiState.novelRatingModel.copy(
                        userNovelRating = rating,
                    ),
                )
            }
        }

        fun updateIsSearchKeywordProceeding(isProceeding: Boolean) {
            uiState.value?.let { uiState ->
                _uiState.value = uiState.copy(
                    keywordsModel = uiState.keywordsModel.copy(
                        isSearchKeywordProceeding = isProceeding,
                    ),
                )
            }
        }

        fun initSearchKeyword() {
            uiState.value?.let { uiState ->
                _uiState.value = uiState.copy(
                    keywordsModel = uiState.keywordsModel.copy(
                        searchResultKeywords = emptyList(),
                        isSearchKeywordProceeding = false,
                        isInitialSearchKeyword = true,
                        isSearchResultKeywordsEmpty = false,
                    ),
                )
            }
        }

        companion object {
            private const val MAX_CHARM_POINT_COUNT = 3
        }
    }
