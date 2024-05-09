package com.teamwss.websoso.ui.main.explore.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.data.repository.FakeNovelRepository
import com.teamwss.websoso.ui.main.explore.normalExplore.model.NormalExploreUiState
import kotlinx.coroutines.launch

class NormalExploreViewModel(
    private val novelRepository: FakeNovelRepository
) : ViewModel() {
    private val _uiState: MutableLiveData<NormalExploreUiState> = MutableLiveData(NormalExploreUiState())
    val uiState: LiveData<NormalExploreUiState> get() = _uiState

    private val _searchContent: MutableLiveData<String> = MutableLiveData()
    val searchContent: MutableLiveData<String> get() = _searchContent

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
            }.onFailure {

            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NormalExploreViewModel(
                    novelRepository = WebsosoApp.getNovelRepository()
                )
            }
        }
    }
}