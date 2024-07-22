package com.teamwss.websoso.ui.main.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeSosoPickRepository
import com.teamwss.websoso.ui.main.explore.model.SosoPickUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
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
}