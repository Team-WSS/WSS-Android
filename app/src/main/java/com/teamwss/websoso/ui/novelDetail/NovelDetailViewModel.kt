package com.teamwss.websoso.ui.novelDetail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R

class NovelDetailViewModel : ViewModel() {
    val imgUrl = "https://img.ridicdn.net/cover/2749028536/xxlarge?dpi=xxhdpi.jpg" // 테스트용 URL
    private val _menuItems = MutableLiveData<MutableList<Int>>(mutableListOf())
    val menuItems: MutableLiveData<MutableList<Int>> get() = _menuItems


    fun getMenuItems() {
        with(_menuItems.value) {
            this?.clear()
            this?.add(R.string.novel_detail_report_error)
            if (true) // 만약 기존에 있는 정보가 있으면~
                this?.add(R.string.novel_detail_remove_evaluate)
        }
    }
}