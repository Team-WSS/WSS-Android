package com.teamwss.websoso.ui.notice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.NoticeRepository
import com.teamwss.websoso.ui.mapper.toUiModel
import com.teamwss.websoso.ui.notice.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeRepository: NoticeRepository,
) : ViewModel() {

    private val _notices = MutableLiveData<List<Notice>>()
    val notices: LiveData<List<Notice>> = _notices

    init {
        fetchNotices()
    }

    private fun fetchNotices() {
        viewModelScope.launch {
            try {
                val entities = noticeRepository.fetchNotices().notices
                _notices.value = entities.toUiModel()
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }
}
