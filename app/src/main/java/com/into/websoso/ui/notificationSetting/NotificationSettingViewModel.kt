package com.into.websoso.ui.notificationSetting

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

    fun updateNotificationEnabled(isEnabled: Boolean) {
        isNotificationEnabled.value = isEnabled

        viewModelScope.launch {
            runCatching {
                notificationRepository.savePushSetting(isEnabled)
            }.onFailure {
                isNotificationEnabled.value = !isEnabled
            }
        }
    }
}
