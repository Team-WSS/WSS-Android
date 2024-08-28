package com.teamwss.websoso.ui.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetNormalExploreResultsUseCase
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NormalExploreViewModel @Inject constructor(
    private val getNormalExploreResultsUseCase: GetNormalExploreResultsUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<NormalExploreUiState> =
        MutableLiveData(NormalExploreUiState())
    val uiState: LiveData<NormalExploreUiState> get() = _uiState

    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

    fun updateSearchResult(isSearchButtonClick: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(loading = true)
            runCatching {
                getNormalExploreResultsUseCase(searchWord.value ?: "", isSearchButtonClick)
            }.onSuccess { results ->
                if (results.novels.isNotEmpty()) {
                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        isLoadable = results.isLoadable,
                        novelCount = results.resultCount,
                        novels = results.novels.map { it.toUi() },
                    )
                    _isNovelResultEmptyBoxVisibility.value = false
                } else {
                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        isLoadable = results.isLoadable,
                        novelCount = results.resultCount,
                        novels = emptyList(),
                    )
                    _isNovelResultEmptyBoxVisibility.value = true
                }

            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false, error = true
                )
                _isNovelResultEmptyBoxVisibility.value = false
            }
        }
    }

    fun validateSearchWordClearButton() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }

    fun updateSearchWordEmpty() {
        _searchWord.value = ""
    }
}