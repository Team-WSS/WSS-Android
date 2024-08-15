package com.teamwss.websoso.ui.profileDisclosure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.UserProfileStatusEntity
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.profileDisclosure.model.ProfileDisclosureUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDisclosureViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<ProfileDisclosureUiState> =
        MutableLiveData(ProfileDisclosureUiState())
    val uiState: LiveData<ProfileDisclosureUiState> get() = _uiState

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
                _uiState.value = uiState.value?.copy(
                    loading = false,
                )
                _isProfilePublic.value = userProfileStatusEntity.isProfilePublic
                initIsProfilePublic = userProfileStatusEntity.isProfilePublic
                updateIsCompleteButtonEnabled()
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = false,
                )
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
        _uiState.value = uiState.value?.copy(
            loading = true,
        )
        viewModelScope.launch {
            runCatching {
                val isProfilePublicValue = isProfilePublic.value ?: initIsProfilePublic.not()
                val userProfileStatusEntity = UserProfileStatusEntity(isProfilePublicValue)
                userRepository.saveUserProfileStatus(userProfileStatusEntity)
            }.onSuccess {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                )
                _isSaveStatusComplete.value = true
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = false,
                )
            }
        }
    }
}