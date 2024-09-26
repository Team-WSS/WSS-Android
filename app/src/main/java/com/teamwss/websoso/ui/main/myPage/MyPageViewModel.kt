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
    private val _myPageUiState = MutableLiveData<MyPageUiState>()
    val myPageUiState: LiveData<MyPageUiState> get() = _myPageUiState

    init {
        updateUserProfile()
    }

    private fun updateUserProfile() {
        _myPageUiState.value = MyPageUiState(loading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyProfile()
            }.onSuccess { myProfileEntity ->
                _myPageUiState.value = MyPageUiState(myProfile = myProfileEntity, loading = false)
            }.onFailure { exception ->
                _myPageUiState.value = MyPageUiState(error = exception.message, loading = false)
            }
        }
    }
}
