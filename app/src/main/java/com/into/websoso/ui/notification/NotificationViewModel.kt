package com.into.websoso.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NotificationInfo
import com.into.websoso.domain.usecase.GetNotificationListUseCase
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.notification.model.NotificationInfoModel
import com.into.websoso.ui.notification.model.NotificationUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel
    @Inject
    constructor(
        private val getNotificationListUseCase: GetNotificationListUseCase,
    ) : ViewModel() {
        private val _notificationUIState: MutableStateFlow<NotificationUIState> =
            MutableStateFlow(NotificationUIState())
        val notificationUIState: StateFlow<NotificationUIState> get() = _notificationUIState

        init {
            updateNotifications()
        }

        fun updateNotifications() {
            when {
                notificationUIState.value.isLoadable.not() -> return
                notificationUIState.value.isLoading -> return
                notificationUIState.value.isLoading.not() -> handleLoadingState()
            }

            viewModelScope.launch {
                runCatching { getNotificationListUseCase(notificationUIState.value.lastNotificationId) }
                    .onSuccess { handleSuccessState(it.getOrDefault(NotificationInfo()).toUi()) }
                    .onFailure { handleFailureState() }
            }
        }

        private fun handleLoadingState() {
            _notificationUIState.value = notificationUIState.value.copy(
                isLoading = true,
                isError = false,
            )
        }

        private fun handleSuccessState(notificationInfo: NotificationInfoModel) {
            val currentUiState = notificationUIState.value
            _notificationUIState.value = currentUiState.copy(
                isLoadable = notificationInfo.isLoadable,
                isLoading = false,
                isError = false,
                lastNotificationId = notificationInfo.lastNotificationId,
                notifications = currentUiState.notifications + notificationInfo.notifications,
            )
        }

        private fun handleFailureState() {
            val currentUiState = notificationUIState.value
            _notificationUIState.value = currentUiState.copy(
                isLoading = false,
                isError = true,
            )
        }

        fun updateReadNotification(notificationId: Long) {
            _notificationUIState.value = notificationUIState.value.copy(
                notifications = notificationUIState.value.notifications.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                },
            )
        }
    }
