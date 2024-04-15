package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R

class NovelDetailViewModel : ViewModel() {
    val imgUrl = "https://img.ridicdn.net/cover/2749028536/xxlarge?dpi=xxhdpi.jpg" // 테스트용 URL
    private val _novelDetailSpinnerMenuItems = MutableLiveData<MutableList<Int>>()
    val novelDetailSpinnerMenuItems: MutableLiveData<MutableList<Int>> get() = _novelDetailSpinnerMenuItems

    fun onClickNovelDetailMenu() {
        val currentItems = mutableListOf<Int>()

        with(currentItems) {
            this.clear()
            this.add(R.string.novel_detail_report_error)
            if (true) // 만약 기존에 있는 정보가 있으면~
                this.add(R.string.novel_detail_remove_evaluate)
        }

        _novelDetailSpinnerMenuItems.value = currentItems
    }
}