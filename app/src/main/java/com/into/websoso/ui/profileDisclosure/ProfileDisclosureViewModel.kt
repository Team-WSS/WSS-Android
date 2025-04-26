package com.into.websoso.ui.profileDisclosure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDisclosureViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private var isInitializeOfProfilePrivate: Boolean = false

        private var _isChangedStatus: MutableLiveData<Boolean> = MutableLiveData(false)
        val isChangedStatus: LiveData<Boolean> get() = _isChangedStatus

        val isProfilePrivate: MutableLiveData<Boolean> = MutableLiveData()

        init {
            updateInitialProfileStatus()
        }

        private fun updateInitialProfileStatus() {
            viewModelScope.launch {
                runCatching {
                    userRepository.fetchUserProfileStatus()
                }.onSuccess { isPublic ->
                    isProfilePrivate.value = !isPublic
                    isInitializeOfProfilePrivate = !isPublic
                }
            }
        }

        fun updateProfileStatus(isPrivate: Boolean) {
            viewModelScope.launch {
                runCatching {
                    userRepository.saveUserProfileStatus(isPrivate.not())
                }.onSuccess {
                    _isChangedStatus.value = isInitializeOfProfilePrivate == !isPrivate
                    isProfilePrivate.value = isPrivate
                }
            }
        }
    }
