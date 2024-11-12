package com.teamwss.websoso.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private var _isAutoLoginSuccess = MutableLiveData(false)
    val isAutoLoginSuccess: LiveData<Boolean> = _isAutoLoginSuccess

    fun autoLogin() {
        viewModelScope.launch {
            if (authRepository.isAutoLogin()) {
                runCatching {
                    authRepository.reissueToken()
                }.onSuccess {
                    _isAutoLoginSuccess.value = true
                }.onFailure {
                    _isAutoLoginSuccess.value = false
                }
            } else {
                _isAutoLoginSuccess.value = false
            }
        }
    }
}