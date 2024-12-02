package com.into.websoso.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private var _isAutoLogin = MutableLiveData(false)
    val isAutoLogin: LiveData<Boolean> get() = _isAutoLogin

    fun autoLogin() {
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
}