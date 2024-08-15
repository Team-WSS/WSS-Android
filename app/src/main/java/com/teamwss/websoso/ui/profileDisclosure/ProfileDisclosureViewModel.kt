package com.teamwss.websoso.ui.profileDisclosure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDisclosureViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _loading: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _error: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _isProfilePublic: MutableLiveData<Boolean> = MutableLiveData()
    val isProfilePublic: LiveData<Boolean> get() = _isProfilePublic

    private val _isCompleteButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isCompleteButtonEnabled: LiveData<Boolean> get() = _isCompleteButtonEnabled

    private var initIsProfilePublic: Boolean = false

    private val _isSaveStatusComplete: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSaveStatusComplete: LiveData<Boolean> get() = _isSaveStatusComplete

    init {
        updateProfileDisclosureStatus()
    }

    private fun updateProfileDisclosureStatus() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserProfileStatus()
            }.onSuccess { userProfileStatusEntity ->
                _loading.value = false
                _isProfilePublic.value = userProfileStatusEntity.isProfilePublic
                initIsProfilePublic = userProfileStatusEntity.isProfilePublic
                updateIsCompleteButtonEnabled()
            }.onFailure {
                _loading.value = false
                _error.value = true
            }
        }
    }

    fun updateProfileStatus() {
        _isProfilePublic.value = _isProfilePublic.value?.not()
        updateIsCompleteButtonEnabled()
    }

    private fun updateIsCompleteButtonEnabled() {
        when (initIsProfilePublic == isProfilePublic.value) {
            true -> _isCompleteButtonEnabled.value = false
            false -> _isCompleteButtonEnabled.value = true
        }
    }

    fun saveProfileDisclosureStatus() {
        viewModelScope.launch {
            _loading.value = true
            runCatching {
                val isProfilePublicValue = isProfilePublic.value ?: initIsProfilePublic.not()
                val userProfileStatusEntity = UserProfileStatusEntity(isProfilePublicValue)
                userRepository.saveUserProfileStatus(userProfileStatusEntity)
            }.onSuccess {
                _loading.value = false
                _isSaveStatusComplete.value = true
            }.onFailure {
                _loading.value = false
                _error.value = true
            }
        }
    }
}