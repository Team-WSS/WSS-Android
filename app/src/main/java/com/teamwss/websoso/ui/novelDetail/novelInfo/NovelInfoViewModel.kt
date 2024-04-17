package com.teamwss.websoso.ui.novelDetail.novelInfo

import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NovelInfoViewModel : ViewModel() {
    private val _viewMoreTextVisibility = MutableLiveData<Boolean>(false)
    val viewMoreTextVisibility: MutableLiveData<Boolean> get() = _viewMoreTextVisibility
    private val _bodyMaxLines = MutableLiveData<Int>(DEFAULT_MAX_LINES)
    val bodyMaxLines: MutableLiveData<Int> get() = _bodyMaxLines

    fun onViewMoreClicked() {
        _bodyMaxLines.value = Int.MAX_VALUE
        _viewMoreTextVisibility.value = false
    }

    fun initViewMoreTextView(lineCount: Int, ellipsisCount: Int) {
        _viewMoreTextVisibility.value = lineCount >= DEFAULT_MAX_LINES && ellipsisCount > 0
    }

    companion object {
        const val DEFAULT_MAX_LINES = 3
    }
}