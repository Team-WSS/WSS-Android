package com.teamwss.websoso.ui.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.NormalExploreEntity
import com.teamwss.websoso.data.repository.FakeNovelRepository
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NormalExploreViewModel @Inject constructor(
    private val novelRepository: FakeNovelRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<NormalExploreUiState> =
        MutableLiveData(NormalExploreUiState())
    val uiState: LiveData<NormalExploreUiState> get() = _uiState

    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

    private val _isNovelResultCountBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultCountBoxVisibility: LiveData<Boolean> get() = _isNovelResultCountBoxVisibility

    private val _isNovelResultEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNovelResultEmptyBoxVisibility: LiveData<Boolean> get() = _isNovelResultEmptyBoxVisibility

    fun updateSearchResult() {
        viewModelScope.launch {
            runCatching {
                novelRepository.normalExploreEmptyDummyData
            }.onSuccess { results ->
                handleSearchSuccess(results)
            }.onFailure {
                handleSearchError()
            }
        }
    }

    private fun handleSearchSuccess(results: NormalExploreEntity) {
        _uiState.value = uiState.value?.copy(
            loading = false,
        )

        if (results.novels.isNotEmpty()) {
            _uiState.value = uiState.value?.copy(
                novels = results.novels,
            )
            _isNovelResultCountBoxVisibility.value = true
        } else {
            _isNovelResultEmptyBoxVisibility.value = true
        }
    }

    private fun handleSearchError() {
        _uiState.value = _uiState.value?.copy(
            loading = false,
            error = true,
        )
    }

    fun validateSearchWordClearButton() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }

    fun updateSearchWordEmpty() {
        _searchWord.value = ""
    }
}