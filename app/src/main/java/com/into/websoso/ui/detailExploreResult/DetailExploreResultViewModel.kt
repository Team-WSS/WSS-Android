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

        private val _selectedGenres: MutableLiveData<List<Genre>?> = MutableLiveData(emptyList())
        private val _isNovelCompleted: MutableLiveData<Boolean?> = MutableLiveData()
        private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
        private val _selectedKeywordIds: MutableLiveData<List<Int>?> = MutableLiveData(emptyList())

        private val _appliedFiltersMessage: MediatorLiveData<String?> = MediatorLiveData()
        val appliedFiltersMessage: LiveData<String?> get() = _appliedFiltersMessage

        private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
        val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

        init {
            _appliedFiltersMessage.apply {
                addSource(_selectedGenres) { updateMessage() }
                addSource(_isNovelCompleted) { updateMessage() }
                addSource(_selectedRating) { updateMessage() }
                addSource(_selectedKeywordIds) { updateMessage() }
            }
        }

        private fun updateMessage() {
            val appliedFilters = mutableListOf<String>()

            if (_selectedGenres.value?.isNotEmpty() == true) appliedFilters.add(GENRES_LABEL)
            if (_isNovelCompleted.value != null) appliedFilters.add(NOVEL_COMPLETED_LABEL)
            if (_selectedRating.value != null) appliedFilters.add(RATING_LABEL)
            if (_selectedKeywordIds.value?.isNotEmpty() == true) appliedFilters.add(KEYWORDS_LABEL)

            _appliedFiltersMessage.value = appliedFilters.joinToString(FILTER_SEPARATOR)
        }

        fun updatePreviousSearchFilteredValue(detailExploreFilteredModel: DetailExploreFilteredModel) {
            _selectedGenres.value = detailExploreFilteredModel.genres
            _isNovelCompleted.value = detailExploreFilteredModel.isCompleted
            _selectedRating.value = detailExploreFilteredModel.novelRating
            _selectedKeywordIds.value = detailExploreFilteredModel.keywordIds

            updateSearchResult(true)
        }

        fun updateSearchResult(isSearchButtonClick: Boolean) {
            if (uiState.value?.isLoadable == false && !isSearchButtonClick) return

            viewModelScope.launch {
                runCatching {
                    getDetailExploreResultUseCase(
                        genres = _selectedGenres.value?.map { it.titleEn },
                        isCompleted = _isNovelCompleted.value,
                        novelRating = _selectedRating.value,
                        keywordIds = _selectedKeywordIds.value,
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
