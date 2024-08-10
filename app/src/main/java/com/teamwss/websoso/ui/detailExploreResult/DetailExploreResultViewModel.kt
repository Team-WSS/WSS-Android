package com.teamwss.websoso.ui.detailExploreResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeNovelRepository
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

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

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