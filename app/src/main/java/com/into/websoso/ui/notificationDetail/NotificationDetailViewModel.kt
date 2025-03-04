package com.into.websoso.ui.notificationDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.ui.notificationDetail.model.NotificationDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationDetailViewModel
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val _notificationDetailUiState: MutableStateFlow<NotificationDetailUiState> =
            MutableStateFlow(NotificationDetailUiState())
        val notificationDetailUiState: StateFlow<NotificationDetailUiState> = _notificationDetailUiState.asStateFlow()

        // TODO: 에러처리 회의 종료 후 반영 필요
        private val _errorFlow = MutableSharedFlow<Throwable>()
        val errorFlow = _errorFlow.asSharedFlow()

        init {
            val notificationId =
                savedStateHandle.get<Long>(NotificationDetailActivity.NOTIFICATION_DETAIL_KEY) ?: DEFAULT_NOTIFICATION_ID
            if (notificationId != DEFAULT_NOTIFICATION_ID) {
                getNotificationDetail(notificationId)
            } else {
                handleInvalidNotificationId()
            }
        }

        private fun getNotificationDetail(notificationId: Long) {
            viewModelScope.launch {
                runCatching {
                    notificationRepository.fetchNotificationDetail(notificationId)
                }.onSuccess { notificationDetail ->
                    _notificationDetailUiState.update { uiState ->
                        uiState.copy(
                            notificationDetail = notificationDetail,
                        )
                    }
                }.onFailure { throwable ->
                    _errorFlow.emit(throwable)
                }
            }
        }

        private fun handleInvalidNotificationId() {
            viewModelScope.launch {
                _errorFlow.emit(IllegalArgumentException("Invalid notification ID"))
            }
        }

        companion object {
            private const val DEFAULT_NOTIFICATION_ID = -1L
        }
    }
