package com.into.websoso.ui.notice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NoticeInfo
import com.into.websoso.domain.usecase.GetNoticeListUseCase
import com.into.websoso.ui.notice.model.NoticeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel
    @Inject
    constructor(
        private val getNoticeListUseCase: GetNoticeListUseCase,
    ) : ViewModel() {
        private val _noticeUiState: MutableStateFlow<NoticeUiState> = MutableStateFlow(NoticeUiState())
        val noticeUiState: StateFlow<NoticeUiState> get() = _noticeUiState

        init {
            updateNotices()
        }

        fun updateNotices() {
            when {
                noticeUiState.value.isLoadable.not() -> return
                noticeUiState.value.isLoading -> return
                noticeUiState.value.isLoading.not() -> handleLoadingState()
            }

            viewModelScope.launch {
                runCatching { getNoticeListUseCase(noticeUiState.value.lastNoticeId) }
                    .onSuccess { handleSuccessState(it.getOrDefault(NoticeInfo())) }
                    .onFailure { handleFailureState() }
            }
        }

        private fun handleLoadingState() {
            _noticeUiState.value = noticeUiState.value.copy(
                isLoading = true,
                isError = false,
            )
        }

        private fun handleSuccessState(noticeInfo: NoticeInfo) {
            val currentUiState = noticeUiState.value
            _noticeUiState.value = currentUiState.copy(
                isLoadable = noticeInfo.isLoadable,
                isLoading = false,
                isError = false,
                lastNoticeId = noticeInfo.lastNoticeId,
                notices = currentUiState.notices + noticeInfo.notices,
            )
        }

        private fun handleFailureState() {
            val currentUiState = noticeUiState.value
            _noticeUiState.value = currentUiState.copy(
                isLoading = false,
                isError = true,
            )
        }

        fun updateReadNotice(noticeId: Long) {
            _noticeUiState.value = noticeUiState.value.copy(
                notices = noticeUiState.value.notices.map {
                    if (it.id == noticeId) it.copy(isRead = true) else it
                },
            )
        }
    }
