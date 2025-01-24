package com.into.websoso.ui.notificationSetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    val isNotificationEnabled: MutableLiveData<Boolean> = MutableLiveData()

    private val _isConnecting: MutableLiveData<Boolean> = MutableLiveData(false)
    val isConnecting: LiveData<Boolean> get() = _isConnecting

    init {
        updateInitializeNotificationEnabled()
    }

    private fun updateInitializeNotificationEnabled() {
        viewModelScope.launch {
            runCatching {
                notificationRepository.fetchPushSetting()
            }.onSuccess { isEnabled ->
                isNotificationEnabled.value = isEnabled
            }
        }
    }

    fun updateNotificationEnabled() {
        _isConnecting.value = true
        viewModelScope.launch {
            runCatching {
                notificationRepository.savePushSetting(isNotificationEnabled.value?.not() ?: true)
            }.onSuccess {
                isNotificationEnabled.value = isNotificationEnabled.value?.not()
                _isConnecting.value = false
            }.onFailure {
                _isConnecting.value = false
            }
        }
    }
}
