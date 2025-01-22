package com.into.websoso.ui.notificationSetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor() : ViewModel() {
    private val _isNotificationEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val isNotificationEnabled: LiveData<Boolean> get() = _isNotificationEnabled

    init {
        updateInitializeNotificationEnabled()
    }

    private fun updateInitializeNotificationEnabled() {
        viewModelScope.launch {
            runCatching {
                // TODO: 알림 설정 상태 가져오기
            }.onSuccess {
                // TODO: _isNotificationEnabled 에 상태 저장
            }
        }
    }

    fun updateNotificationEnabled() {
        viewModelScope.launch {
            runCatching {
                // TODO: 알림 설정 상태 업데이트
            }.onSuccess {
                _isNotificationEnabled.value = _isNotificationEnabled.value?.not()
            }
        }
    }
}
