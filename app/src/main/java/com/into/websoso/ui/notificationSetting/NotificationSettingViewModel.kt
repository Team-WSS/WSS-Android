package com.into.websoso.ui.notificationSetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) : ViewModel() {
        val isNotificationPushEnabled: MutableLiveData<Boolean> = MutableLiveData()

        init {
            updateInitializeNotificationPushEnabled()
        }

        private fun updateInitializeNotificationPushEnabled() {
            viewModelScope.launch {
                runCatching {
                    notificationRepository.fetchUserPushEnabled()
                }.onSuccess { isEnabled ->
                    isNotificationPushEnabled.value = isEnabled
                }
            }
        }

        fun updateNotificationPushEnabled(isEnabled: Boolean) {
            isNotificationPushEnabled.value = isEnabled

            viewModelScope.launch {
                runCatching {
                    notificationRepository.saveUserPushEnabled(isEnabled)
                }.onFailure {
                    isNotificationPushEnabled.value = !isEnabled
                }
            }
        }
    }
