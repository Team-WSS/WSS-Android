package com.teamwss.websoso.ui.otherUserPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.OtherUserProfileEntity
import com.teamwss.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _otherUserProfile = MutableLiveData<OtherUserProfileEntity>()
    val otherUserProfile: LiveData<OtherUserProfileEntity> get() = _otherUserProfile

    private val _userId: MutableLiveData<Long> = MutableLiveData()
    val userId: LiveData<Long> get() = _userId

    private val _isBlockedCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val isBlockedCompleted: LiveData<Boolean> get() = _isBlockedCompleted

    fun updateUserId(userId: Long) {
        _userId.value = userId

        updateUserProfile()
    }

    private fun updateUserProfile() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchOtherUserProfile(userId.value ?: 0L)
            }.onSuccess { otherUserProfile ->
                _otherUserProfile.value = otherUserProfile
            }.onFailure { exception ->
            }
        }
    }

    fun updateBlockedUser() {
        viewModelScope.launch {
            runCatching {
                userRepository.saveBlockUser(userId.value ?: 0L)
            }.onSuccess {
                _isBlockedCompleted.value = true
            }.onFailure {}
        }
    }
}