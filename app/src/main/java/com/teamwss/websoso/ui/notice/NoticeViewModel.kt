package com.teamwss.websoso.ui.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NoticeRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.notice.model.NoticeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
) : ViewModel() {

    private val _noticeUiState: MutableLiveData<NoticeUiState> = MutableLiveData(NoticeUiState())
    val noticeUiState: LiveData<NoticeUiState> get() = _noticeUiState

    init {
        updateNotices()
    }

    private fun updateNotices() {
        noticeUiState.value?.let { noticeUiState ->
            viewModelScope.launch {
                runCatching {
                    noticeRepository.fetchNotices().notices
                }.onSuccess { notices ->
                    _noticeUiState.value = noticeUiState.copy(
                        loading = false,
                        notices = notices.map { it.toUi() }
                    )
                }.onFailure {
                    _noticeUiState.value = noticeUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }
}
