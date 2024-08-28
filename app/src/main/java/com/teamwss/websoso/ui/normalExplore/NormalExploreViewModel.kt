package com.teamwss.websoso.ui.normalExplore

import android.util.Log
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

    fun updateSearchResult() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(loading = true)

            runCatching {
                val isLoadable: Boolean = uiState.value?.isLoadable == true
                getNormalExploreResultsUseCase(searchWord.value ?: "", isLoadable)
            }.onSuccess { results ->
                if (results.novels.isNotEmpty()) {
                    Log.d("moongchi", "updateSearchResult: ${results.novels}")
                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        isLoadable = results.isLoadable,
                        novels = results.novels.map { it.toUi() },
                    )
                } else {
                    _uiState.value = _uiState.value?.copy(loading = false)
                    _isNovelResultEmptyBoxVisibility.value = true
                }
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true
                )
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