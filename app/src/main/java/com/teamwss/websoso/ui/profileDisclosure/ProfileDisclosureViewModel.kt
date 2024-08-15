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
                _isProfilePublic.value = userProfileStatusEntity.isProfilePublic
            }.onFailure {

            }
        }
    }

}