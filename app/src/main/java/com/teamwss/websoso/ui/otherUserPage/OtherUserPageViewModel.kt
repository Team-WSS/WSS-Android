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

    private val userId: Long = getUserId()

    private val _isBlockedCompleted: MutableLiveData<Boolean> = MutableLiveData()
    val isBlockedCompleted: LiveData<Boolean> get() = _isBlockedCompleted

    init {
        updateUserProfile(userId)
    }

    private fun updateUserProfile(userId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchOtherUserProfile(userId)
            }.onSuccess { otherUserProfile ->
                _otherUserProfile.value = otherUserProfile
            }.onFailure { exception ->
            }
        }
    }

    private fun getUserId(): Long {
        return 3L
    }

    fun updateBlockedUser() {
        viewModelScope.launch {
            runCatching {
                userRepository.saveBlockUser(userId)
            }.onSuccess {
                _isBlockedCompleted.value = true
            }.onFailure {}
        }
    }
}