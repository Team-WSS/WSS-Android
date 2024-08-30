package com.teamwss.websoso.ui.detailExploreResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetDetailExploreResultUseCase
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
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

    private val _selectedGenres: MutableLiveData<MutableList<Genre>> =
        MutableLiveData(mutableListOf())
    val selectedGenres: LiveData<List<Genre>> get() = _selectedGenres.map { it.toList() }

    private val _selectedSeriesStatus: MutableLiveData<Boolean?> = MutableLiveData()
    val selectedStatus: LiveData<Boolean?> get() = _selectedSeriesStatus

    private val _selectedRating: MutableLiveData<Float?> = MutableLiveData()
    val selectedRating: LiveData<Float?> get() = _selectedRating

    val ratings: List<Float> = listOf(3.5f, 4.0f, 4.5f, 4.8f)

    private val _isInfoChipSelected: MediatorLiveData<Boolean> = MediatorLiveData(false)
    val isInfoChipSelected: LiveData<Boolean> get() = _isInfoChipSelected

    private val _isKeywordChipSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val isKeywordChipSelected: LiveData<Boolean> get() = _isKeywordChipSelected

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

    fun updatePreviousSearchFilteredValue(detailExploreFilteredModel: DetailExploreFilteredModel) {
        _selectedGenres.value = detailExploreFilteredModel.genres?.toMutableList()
        _selectedSeriesStatus.value = detailExploreFilteredModel.isCompleted
        _selectedRating.value = detailExploreFilteredModel.novelRating
        _uiState.value = uiState.value?.copy(
            selectedKeywords = detailExploreFilteredModel.keywordIds?.toList() ?: emptyList(),
        )

        updateSearchResult(true)
    }

    fun updateSearchResult(isSearchButtonClick: Boolean) {
        if (uiState.value?.isLoadable == false && !isSearchButtonClick) return

        viewModelScope.launch {
            runCatching {
                getDetailExploreResultUseCase(
                    genres = selectedGenres.value?.map { it.toString() },
                    isCompleted = selectedStatus.value,
                    novelRating = selectedRating.value,
                    keywordIds = uiState.value?.selectedKeywords,
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
}