package com.teamwss.websoso.ui.detailExploreResult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelRepository
import com.teamwss.websoso.ui.detailExplore.info.model.Genre
import com.teamwss.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailExploreResultViewModel @Inject constructor(
    private val novelRepository: FakeNovelRepository,
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

    fun updateDetailExploreFilteredValue(
        genre: Array<Genre>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: Array<Int>?
    ) {
        _selectedGenres.value = genre?.toMutableList()
        _selectedSeriesStatus.value = isCompleted
        _selectedRating.value = novelRating
        _uiState.value = uiState.value?.copy(
            selectedKeywords = keywordIds?.toList() ?: emptyList(),
        )
        Log.d(
            "moongchi",
            "updateDetailExploreFilteredValue: genres=${genre?.joinToString()} isCompleted=$isCompleted novelRating=$novelRating keywordIds=${keywordIds?.joinToString()}"
        )
    }

    fun updateSearchResult() {
        viewModelScope.launch {
            runCatching {
                novelRepository.fetchDetailExploreResult(
                    page = 1,
                    size = 20,
                    genres = listOf("로맨스"),
                    isCompleted = false,
                    novelRating = 4.5f,
                    keywordIds = listOf() // TODO 이건 더미임요 서버 붙이면서 추후 제거 예정
                )
            }.onSuccess { results ->
                when (results.novels.isNotEmpty()) {
                    true -> {
                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            novels = results.novels,
                        )
                    }

                    false -> {
                        _uiState.value = uiState.value?.copy(
                            loading = false,
                        )

                        _isNovelResultEmptyBoxVisibility.value = true
                    }
                }
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }
}