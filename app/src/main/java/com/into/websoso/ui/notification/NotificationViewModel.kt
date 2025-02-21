package com.into.websoso.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NotificationInfo
import com.into.websoso.domain.usecase.GetNotificationListUseCase
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.notification.model.NotificationInfoModel
import com.into.websoso.ui.notification.model.NotificationUiState
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
        private val _notificationUiState: MutableStateFlow<NotificationUiState> = MutableStateFlow(NotificationUiState())
        val notificationUiState: StateFlow<NotificationUiState> get() = _notificationUiState

        init {
            updateNotifications()
        }

        fun updateNotifications() {
            when {
                notificationUiState.value.isLoadable.not() -> return
                notificationUiState.value.isLoading -> return
                notificationUiState.value.isLoading.not() -> handleLoadingState()
            }

            viewModelScope.launch {
                runCatching { getNotificationListUseCase(notificationUiState.value.lastNotificationId) }
                    .onSuccess { handleSuccessState(it.getOrDefault(NotificationInfo()).toUi()) }
                    .onFailure { handleFailureState() }
            }
        }

        private fun handleLoadingState() {
            _notificationUiState.value = notificationUiState.value.copy(
                isLoading = true,
                isError = false,
            )
        }

        private fun handleSuccessState(notificationInfo: NotificationInfoModel) {
            val currentUiState = notificationUiState.value
            _notificationUiState.value = currentUiState.copy(
                isLoadable = notificationInfo.isLoadable,
                isLoading = false,
                isError = false,
                lastNotificationId = notificationInfo.lastNotificationId,
                notifications = currentUiState.notifications + notificationInfo.notifications,
            )
        }

        private fun handleFailureState() {
            val currentUiState = notificationUiState.value
            _notificationUiState.value = currentUiState.copy(
                isLoading = false,
                isError = true,
            )
        }

        fun updateReadNotification(notificationId: Long) {
            _notificationUiState.value = notificationUiState.value.copy(
                notifications = notificationUiState.value.notifications.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                },
            )
        }
    }
