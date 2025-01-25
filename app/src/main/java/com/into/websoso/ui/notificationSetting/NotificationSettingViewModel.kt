package com.into.websoso.ui.notificationSetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.PushMessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel
    @Inject
    constructor(
        private val pushMessageRepository: PushMessageRepository,
    ) : ViewModel() {
        val isNotificationPushEnabled: MutableLiveData<Boolean> = MutableLiveData()

        init {
            updateInitializeNotificationPushEnabled()
        }

        private fun updateInitializeNotificationPushEnabled() {
            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.fetchUserPushEnabled()
                }.onSuccess { isEnabled ->
                    isNotificationPushEnabled.value = isEnabled
                }
            }
        }

        fun updateNotificationPushEnabled(isEnabled: Boolean) {
            isNotificationPushEnabled.value = isEnabled

            viewModelScope.launch {
                runCatching {
                    pushMessageRepository.saveUserPushEnabled(isEnabled)
                }.onFailure {
                    isNotificationPushEnabled.value = !isEnabled
                }
            }
        }
    }
