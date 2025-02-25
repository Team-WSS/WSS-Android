package com.into.websoso.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val versionRepository: VersionRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _isUpdateRequired: MutableLiveData<Boolean> = MutableLiveData()
        val isUpdateRequired: LiveData<Boolean> get() = _isUpdateRequired

        private var _isAutoLogin: MutableLiveData<Boolean> = MutableLiveData()
        val isAutoLogin: LiveData<Boolean> get() = _isAutoLogin

        private var _error: MutableLiveData<Boolean> = MutableLiveData(false)
        val error: LiveData<Boolean> get() = _error

        init {
            checkAndUpdateVersion()
        }

        private fun checkAndUpdateVersion() {
            viewModelScope.launch {
                runCatching {
                    versionRepository.isUpdateRequired()
                }.onSuccess { isRequired ->
                    _isUpdateRequired.value = isRequired
                }
            }
        }

        // 토큰 만료 확인용 - 추후 로직 수정 필요
        fun updateMyProfile() {
            viewModelScope.launch {
                runCatching {
                    userRepository.fetchMyProfile()
                }.onSuccess {
                    autoLogin()
                }.onFailure {
                    authRepository.clearTokens()
                    _error.value = true
                }
            }
        }

        private fun autoLogin() {
            viewModelScope.launch {
                if (authRepository.isAutoLogin) {
                    runCatching {
                        authRepository.reissueToken()
                    }.onSuccess {
                        _isAutoLogin.value = true
                    }.onFailure {
                        _isAutoLogin.value = false
                    }
                } else {
                    _isAutoLogin.value = false
                }
            }
        }

        fun updateUserDeviceIdentifier(deviceIdentifier: String) {
            viewModelScope.launch {
                runCatching {
                    userRepository.saveUserDeviceIdentifier(deviceIdentifier)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
