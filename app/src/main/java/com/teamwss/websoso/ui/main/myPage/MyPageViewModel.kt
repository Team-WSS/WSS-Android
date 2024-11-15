package com.teamwss.websoso.ui.main.myPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.model.MyPageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableLiveData<MyPageUiState>()
    val uiState: LiveData<MyPageUiState> get() = _uiState

    init {
        updateUserProfile()
    }

    fun updateUserProfile() {
        _uiState.value = uiState.value?.copy(loading = true) ?: MyPageUiState(loading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyProfile()
            }.onSuccess { myProfileEntity ->
                _uiState.value = uiState.value?.copy(
                    myProfile = myProfileEntity,
                    loading = false,
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    error = true,
                    loading = false,
                )
            }
        }
    }
}