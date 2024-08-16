package com.teamwss.websoso.ui.withdraw.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.withdraw.first.model.WithdrawFirstUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawFirstViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<WithdrawFirstUiState> =
        MutableLiveData(WithdrawFirstUiState())
    val uiState: LiveData<WithdrawFirstUiState> get() = _uiState

    init {
        updateNovelStats()
    }

    private fun updateNovelStats() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserNovelStats()
            }.onSuccess { userNovelStatsEntity ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    userNovelStats = userNovelStatsEntity.toUi(),
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