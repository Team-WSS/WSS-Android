package com.teamwss.websoso.ui.main.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.data.repository.FakeSosoPickRepository
import com.teamwss.websoso.ui.main.explore.model.SosoPickUiState
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val fakeSosoPickRepository: FakeSosoPickRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<SosoPickUiState> = MutableLiveData(SosoPickUiState())
    val uiState: LiveData<SosoPickUiState> get() = _uiState

    init {
        fetchSosoPick()
    }

    private fun fetchSosoPick() {
        viewModelScope.launch {
            runCatching {
                fakeSosoPickRepository.dummyData
            }.onSuccess { sosoPicks ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    sosoPicks = sosoPicks.novels
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ExploreViewModel(
                    fakeSosoPickRepository = WebsosoApp.getSosoPickRepository()
                )
            }
        }
    }
}