package com.teamwss.websoso.ui.detailExploreResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetDetailExploreResultUseCase
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExplore.info.model.SeriesStatus
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreFilteredModel
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreResultViewModel @Inject constructor(
    private val getDetailExploreResultUseCase: GetDetailExploreResultUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<DetailExploreResultUiState> =
        MutableLiveData(DetailExploreResultUiState())
    val uiState: LiveData<DetailExploreResultUiState> get() = _uiState

    private val _selectedGenres: MutableLiveData<MutableList<Genre>?> =
        MutableLiveData(mutableListOf())
    val selectedGenres: LiveData<List<Genre>?> get() = _selectedGenres.map { it?.toList() }

    private val _selectedSeriesStatus: MutableLiveData<Boolean?> = MutableLiveData()
    val selectedStatus: LiveData<Boolean?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _selectedKeywordIds: MutableLiveData<MutableList<Int>?> =
        MutableLiveData(mutableListOf())
    val selectedKeywordIds: LiveData<List<Int>?> get() = _selectedKeywordIds.map { it?.toList() }

    private val _appliedFiltersMessage: MediatorLiveData<String?> = MediatorLiveData()
    val appliedFiltersMessage: LiveData<String?> get() = _appliedFiltersMessage

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    private val _isKeywordChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isKeywordChipSelected: LiveData<Boolean> get() = _isKeywordChipSelected

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

    init {
        _appliedFiltersMessage.apply {
            addSource(_selectedGenres) { updateMessage() }
            addSource(_selectedSeriesStatus) { updateMessage() }
            addSource(_selectedRating) { updateMessage() }
            addSource(_selectedKeywordIds) { updateMessage() }
        }

        _isInfoChipSelected.apply {
            addSource(_selectedGenres) { isInfoChipSelectedEnabled() }
            addSource(_selectedSeriesStatus) { isInfoChipSelectedEnabled() }
            addSource(_selectedRating) { isInfoChipSelectedEnabled() }
        }

        _isKeywordChipSelected.addSource(_selectedKeywordIds) { isKeywordChipSelectedEnabled() }
    }

    private fun updateMessage() {
        val appliedFilters = mutableListOf<String>()

        _selectedGenres.value?.let { genres ->
            if (genres.isNotEmpty()) {
                appliedFilters.add("장르")
            }
        }

        _selectedSeriesStatus.value?.let { status ->
            appliedFilters.add("연재상태")
        }

        _selectedRating.value?.let { rating ->
            appliedFilters.add("별점")
        }

        _selectedKeywordIds.value?.let { keywords ->
            if (keywords.isNotEmpty()) {
                appliedFilters.add("키워드")
            }
        }

        _appliedFiltersMessage.value = appliedFilters.joinToString(", ")
    }

    private fun isInfoChipSelectedEnabled() {
        val isGenreChipSelected: Boolean = _selectedGenres.value?.isNotEmpty() == true
        val isStatusChipSelected: Boolean = _selectedSeriesStatus.value != null
        val isRatingChipSelected: Boolean = _selectedRating.value != null

        _isInfoChipSelected.value =
            isGenreChipSelected || isStatusChipSelected || isRatingChipSelected
    }

    private fun isKeywordChipSelectedEnabled() {
        _isKeywordChipSelected.value = selectedKeywordIds.value?.isNotEmpty()
    }

    fun updatePreviousSearchFilteredValue(detailExploreFilteredModel: DetailExploreFilteredModel) {
        _selectedGenres.value = detailExploreFilteredModel.genres?.toMutableList()
        _selectedSeriesStatus.value = detailExploreFilteredModel.isCompleted
        _selectedRating.value = detailExploreFilteredModel.novelRating
        _selectedKeywordIds.value = detailExploreFilteredModel.keywordIds?.toMutableList()

        updateSearchResult(true)
    }

    fun updateSearchResult(isSearchButtonClick: Boolean) {
        if (uiState.value?.isLoadable == false && !isSearchButtonClick) return

        viewModelScope.launch {
            runCatching {
                getDetailExploreResultUseCase(
                    genres = selectedGenres.value?.map { it.titleEn },
                    isCompleted = selectedStatus.value,
                    novelRating = selectedRating.value,
                    keywordIds = selectedKeywordIds.value,
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

    fun updateSelectedInfoValueClear() {
        _selectedGenres.value = mutableListOf()
        _selectedSeriesStatus.value = null
        _selectedRating.value = null
    }

    fun updateSelectedGenres(genre: Genre) {
        val currentGenres = _selectedGenres.value?.toMutableList() ?: mutableListOf()

        _selectedGenres.value = when (currentGenres.contains(genre)) {
            true -> {
                currentGenres.remove(genre)
                currentGenres
            }

            false -> {
                currentGenres.add(genre)
                currentGenres
            }
        }
    }

    fun updateSelectedSeriesStatus(status: SeriesStatus?) {
        _selectedSeriesStatus.value = status?.isCompleted
    }

    fun updateSelectedRating(rating: Float?) {
        _selectedRating.value = rating
    }
}