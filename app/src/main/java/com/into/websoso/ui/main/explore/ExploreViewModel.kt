package com.into.websoso.ui.main.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.ui.main.explore.model.SosoPickUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel
    @Inject
    constructor(
        private val novelRepository: NovelRepository,
    ) : ViewModel() {
        private val _uiState: MutableLiveData<SosoPickUiState> = MutableLiveData(SosoPickUiState())
        val uiState: LiveData<SosoPickUiState> get() = _uiState

        init {
            updateSosoPicks()
        }

        fun updateSosoPicks() {
            viewModelScope.launch {
                runCatching {
                    novelRepository.fetchSosoPicks()
                }.onSuccess { sosoPicks ->
                    _uiState.value = uiState.value?.copy(
                        loading = false,
                        sosoPicks = sosoPicks.novels,
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
