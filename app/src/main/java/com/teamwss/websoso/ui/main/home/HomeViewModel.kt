package com.teamwss.websoso.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.ui.main.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val novelRepository: NovelRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData(HomeUiState())
    val uiState: LiveData<HomeUiState> get() = _uiState

    init {
        updatePopularNovels()
    }

    private fun updatePopularNovels() {
        viewModelScope.launch {
            runCatching {
                novelRepository.fetchPopularNovels()
            }.onSuccess { popularNovels ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    popularNovels = popularNovels.popularNovels
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }
}