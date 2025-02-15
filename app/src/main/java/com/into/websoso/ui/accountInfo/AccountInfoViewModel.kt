package com.into.websoso.ui.accountInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val authRepository: AuthRepository,
        private val pushMessageRepository: PushMessageRepository,
    ) : ViewModel() {
        private val _userEmail: MutableLiveData<String> = MutableLiveData()
        val userEmail: LiveData<String> get() = _userEmail

        private val _isLogoutSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
        val isLogoutSuccess: LiveData<Boolean> get() = _isLogoutSuccess

        init {
            updateUserEmail()
        }

        private fun updateUserEmail() {
            viewModelScope.launch {
                runCatching {
                    userRepository.fetchUserInfoDetail()
                }.onSuccess { userInfo ->
                    _userEmail.value = userInfo.email
                }.onFailure {
                }
            }
        }

        fun logout() {
            viewModelScope.launch {
                runCatching {
                    val userDeviceIdentifier = userRepository.fetchUserDeviceIdentifier()
                    authRepository.logout(userDeviceIdentifier)
                }.onSuccess {
                    _isLogoutSuccess.value = true
                    authRepository.updateIsAutoLogin(false)
                    pushMessageRepository.clearFCMToken()
                }.onFailure {
                    _isLogoutSuccess.value = false
                }
            }
        }
    }
