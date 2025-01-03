package com.into.websoso.ui.otherUserPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.otherUserPage.model.OtherUserPageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(OtherUserPageUiState())
    val uiState: LiveData<OtherUserPageUiState> get() = _uiState

    private var userId: Long = 0L

    private val _isWithdrawUser: MutableLiveData<Boolean> = MutableLiveData(false)
    val isWithdrawUser: LiveData<Boolean> get() = _isWithdrawUser

    fun updateUserId(newUserId: Long) {
        userId = newUserId
        _uiState.value = _uiState.value?.copy(isLoading = true)
        updateUserProfile(newUserId)
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
            }.onFailure { exception ->
                if (exception.message?.contains(WITHDRAW_USER_CODE) == true) {
                    _isWithdrawUser.value = true
                    return@onFailure
                }
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
                userRepository.saveBlockUser(userId)
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

    companion object {
        private const val WITHDRAW_USER_CODE = "403"
    }
}
