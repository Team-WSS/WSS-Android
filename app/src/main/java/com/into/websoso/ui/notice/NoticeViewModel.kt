package com.into.websoso.ui.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.domain.model.NoticeInfo
import com.into.websoso.domain.usecase.GetNoticeListUseCase
import com.into.websoso.ui.notice.model.NoticeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val getNoticeListUseCase: GetNoticeListUseCase,
) : ViewModel() {

    private val _noticeUiState: MutableLiveData<NoticeUiState> = MutableLiveData(NoticeUiState())
    val noticeUiState: LiveData<NoticeUiState> get() = _noticeUiState

    init {
        updateNotices()
    }

    private fun updateNotices() {
        when (noticeUiState.value?.isLoading == true) {
            true -> return
            false -> handleLoadingState()
        }

        viewModelScope.launch {
            runCatching { getNoticeListUseCase() }
                .onSuccess { handleSuccessState(it) }
                .onFailure { handleFailureState() }
        }
    }

    private fun handleLoadingState() {
        _noticeUiState.value = noticeUiState.value?.copy(
            isLoading = true,
            isError = false,
        )
    }

    private fun handleSuccessState(result: Result<NoticeInfo>) {
        val currentUiState = noticeUiState.value ?: return
        val noticeInfo = result.getOrDefault(NoticeInfo())
        _noticeUiState.value = currentUiState.copy(
            isLoading = false,
            isError = false,
            noticeInfo = currentUiState.noticeInfo.copy(
                notices = currentUiState.noticeInfo.notices + noticeInfo.notices,
            ),
        )
    }

    private fun handleFailureState() {
        val currentUiState = noticeUiState.value ?: return
        _noticeUiState.value = currentUiState.copy(
            isLoading = false,
            isError = true,
        )
    }
}
