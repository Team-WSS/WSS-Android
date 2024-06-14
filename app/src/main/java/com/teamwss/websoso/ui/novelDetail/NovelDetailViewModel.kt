package com.teamwss.websoso.ui.novelDetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class NovelDetailViewModel : ViewModel() {
    // 테스트용 변수
    val coverImgUrl =
        "https://github.com/Team-WSS/WSS-Android/assets/127238018/9904c8f8-623e-4a24-90f9-08f0144f5a1f"
    val genreImgUrl =
        "https://github.com/Team-WSS/WSS-Android/assets/127238018/b060e980-2be2-4f5c-9f85-f3bc2fcb9686"
    private var _userNovelId: MutableLiveData<Int?> = MutableLiveData(NULL_USER_NOVEL_ID)
    val userNovelId: MutableLiveData<Int?> get() = _userNovelId

    companion object {
        const val NULL_USER_NOVEL_ID = -1 // 나중에 데이터 레이어로 옮기자 얘도 같이~~
    }
}