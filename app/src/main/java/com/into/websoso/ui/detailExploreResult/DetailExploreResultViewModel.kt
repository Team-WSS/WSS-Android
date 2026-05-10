package com.into.websoso.ui.detailExploreResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.usecase.GetDetailExploreResultUseCase
import com.into.websoso.ui.detailExplore.info.model.Genre
import com.into.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import com.into.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreResultViewModel
    @Inject
    constructor(
        private val getDetailExploreResultUseCase: GetDetailExploreResultUseCase,
    ) : ViewModel() {
        private val _uiState: MutableLiveData<DetailExploreResultUiState> =
            MutableLiveData(DetailExploreResultUiState())
        val uiState: LiveData<DetailExploreResultUiState> get() = _uiState

        private val filterGenres: MutableLiveData<List<Genre>?> = MutableLiveData(emptyList())
        private val filterIsNovelCompleted: MutableLiveData<Boolean?> = MutableLiveData()
        private val filterRatingStart: MutableLiveData<Float> = MutableLiveData(DetailExploreFilteredModel.RATING_MIN_DEFAULT)
        private val filterRatingEnd: MutableLiveData<Float> = MutableLiveData(DetailExploreFilteredModel.RATING_MAX_DEFAULT)
        private val filterKeywordIds: MutableLiveData<List<Int>?> = MutableLiveData(emptyList())

        private val _appliedFiltersMessage: MediatorLiveData<String?> = MediatorLiveData()
        val appliedFiltersMessage: LiveData<String?> get() = _appliedFiltersMessage

        private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
        val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

        init {
            _appliedFiltersMessage.apply {
                addSource(filterGenres) { updateMessage() }
                addSource(filterIsNovelCompleted) { updateMessage() }
                addSource(filterRatingStart) { updateMessage() }
                addSource(filterRatingEnd) { updateMessage() }
                addSource(filterKeywordIds) { updateMessage() }
            }
        }

        private fun updateMessage() {
            val appliedFilters = mutableListOf<String>()

            if (filterGenres.value?.isNotEmpty() == true) appliedFilters.add(GENRES_LABEL)
            if (filterIsNovelCompleted.value != null) appliedFilters.add(NOVEL_COMPLETED_LABEL)
            appliedFilters.add(RATING_LABEL)
            if (filterKeywordIds.value?.isNotEmpty() == true) appliedFilters.add(KEYWORDS_LABEL)

            _appliedFiltersMessage.value = appliedFilters.joinToString(FILTER_SEPARATOR)
        }

        fun updatePreviousSearchFilteredValue(detailExploreFilteredModel: DetailExploreFilteredModel) {
            filterGenres.value = detailExploreFilteredModel.genres
            filterIsNovelCompleted.value = detailExploreFilteredModel.isCompleted
            filterRatingStart.value = detailExploreFilteredModel.novelRatingStart
            filterRatingEnd.value = detailExploreFilteredModel.novelRatingEnd
            filterKeywordIds.value = detailExploreFilteredModel.keywordIds

            updateSearchResult(true)
        }

        fun updateSearchResult(isSearchButtonClick: Boolean) {
            if (uiState.value?.isLoadable == false && !isSearchButtonClick) return

            viewModelScope.launch {
                runCatching {
                    getDetailExploreResultUseCase(
                        genres = filterGenres.value?.map { it.titleEn },
                        isCompleted = filterIsNovelCompleted.value,
                        novelRatingStart = filterRatingStart.value ?: DetailExploreFilteredModel.RATING_MIN_DEFAULT,
                        novelRatingEnd = filterRatingEnd.value ?: DetailExploreFilteredModel.RATING_MAX_DEFAULT,
                        keywordIds = filterKeywordIds.value,
                        isSearchButtonClick = isSearchButtonClick,
                    )
                }.onSuccess { results ->
                    when (results.novels.isNotEmpty()) {
                        true -> {
                            _uiState.value = uiState.value?.copy(
                                loading = false,
                                isLoadable = results.isLoadable,
                                novels = results.novels.map { it.toUi() },
                                novelCount = results.resultCount,
                            )
                            _isNovelResultEmptyBoxVisibility.value = false
                        }

                        false -> {
                            _uiState.value = uiState.value?.copy(
                                loading = false,
                                isLoadable = results.isLoadable,
                                novelCount = results.resultCount,
                                novels = emptyList(),
                            )
                            _isNovelResultEmptyBoxVisibility.value = true
                        }
                    }
                }.onFailure {
                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        error = true,
                    )
                    _isNovelResultEmptyBoxVisibility.value = false
                }
            }
        }

        companion object {
            private const val GENRES_LABEL = "장르"
            private const val NOVEL_COMPLETED_LABEL = "연재상태"
            private const val RATING_LABEL = "별점"
            private const val KEYWORDS_LABEL = "키워드"
            private const val FILTER_SEPARATOR = ", "
        }
    }
