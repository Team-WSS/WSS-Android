package com.teamwss.websoso.ui.otherUserPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.otherUserPage.model.OtherUserPageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(OtherUserPageUiState())
    val uiState: LiveData<OtherUserPageUiState> get() = _uiState

    fun updateUserId(userId: Long) {
        _uiState.value = _uiState.value?.copy(userId = userId, isLoading = true)
        updateUserProfile(userId)
    }

    private fun updateUserProfile(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchOtherUserProfile(userId)
            }.onSuccess { otherUserProfile ->
                _uiState.value = _uiState.value?.copy(
                    otherUserProfile = otherUserProfile,
                    isLoading = false,
                    error = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    fun updateBlockedUser() {
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.saveBlockUser(_uiState.value?.userId ?: 0L)
            }.onSuccess {
                _uiState.value = _uiState.value?.copy(
                    isBlockedCompleted = true,
                    isLoading = false,
                    error = false,
                )
            }.onFailure {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }
}
