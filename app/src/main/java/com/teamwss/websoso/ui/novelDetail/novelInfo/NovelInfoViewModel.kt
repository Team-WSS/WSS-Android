package com.teamwss.websoso.ui.novelDetail.novelInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NovelInfoViewModel : ViewModel() {
    private val _viewMoreTextVisibility = MutableLiveData<Boolean>(false)
    val viewMoreTextVisibility: MutableLiveData<Boolean> get() = _viewMoreTextVisibility
    private val _isViewMoreEnabled = MutableLiveData<Boolean>(true)
    val isViewMoreEnabled: MutableLiveData<Boolean> get() = _isViewMoreEnabled
    private val _bodyMaxLines = MutableLiveData<Int>(DEFAULT_MAX_LINES)
    val bodyMaxLines: MutableLiveData<Int> get() = _bodyMaxLines

    fun onViewMoreClicked() {
        if (_isViewMoreEnabled.value == true) {
            _isViewMoreEnabled.value = false
            _bodyMaxLines.value = Int.MAX_VALUE
            return
        }
        _isViewMoreEnabled.value = true
        _bodyMaxLines.value = DEFAULT_MAX_LINES
    }

    fun initViewMoreTextView(lineCount: Int, ellipsisCount: Int) {
        _viewMoreTextVisibility.value = lineCount >= DEFAULT_MAX_LINES && ellipsisCount > 0
    }

    companion object {
        const val DEFAULT_MAX_LINES = 3
    }
}