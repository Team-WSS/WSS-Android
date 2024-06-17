package com.teamwss.websoso.ui.myPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.data.model.MyPageProfileData

class MyPageViewModel : ViewModel() {
    private val _myPageProfile: MutableLiveData<MyPageProfileData> =
        MutableLiveData<MyPageProfileData>().apply {
            value = MyPageProfileData(
                avatarImage = "https://velog.velcdn.com/images/yeonjeen/post/73663b4d-ead3-41fa-bab9-4fd1a1651f91/image.png",
                genrePreferences = listOf("Fantasy", "Romance", "Action"),
                intro = "안녕 나는 사실 연진이야",
                nickname = "연진"
            )
        }
    val myPageProfile: LiveData<MyPageProfileData> = _myPageProfile
}