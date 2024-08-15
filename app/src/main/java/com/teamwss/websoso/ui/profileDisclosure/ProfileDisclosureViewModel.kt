package com.teamwss.websoso.ui.profileDisclosure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        fetchProfileDisclosureStatus()
    }

    private fun fetchProfileDisclosureStatus() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserProfileStatus()
            }.onSuccess { userProfileStatusEntity ->
                _loading.value = false
                _isProfilePublic.value = userProfileStatusEntity.isProfilePublic
            }.onFailure {
                _loading.value = false
                _error.value = true
            }
        }
    }
}