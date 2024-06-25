package com.teamwss.websoso.ui.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun fetchNormalExploreResult() {
        viewModelScope.launch {
            runCatching {
                novelRepository.normalExploreResultDummyData
            }.onSuccess { results ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    novelCount = results.resultCount,
                    novels = results.novels,
                )
                _isNovelResultCountBoxVisibility.value = true
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun validateSearchWordClearButton() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }
}