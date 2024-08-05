package com.teamwss.websoso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R

class LoginViewModel : ViewModel() {
    private val _loginImages = MutableLiveData<List<Int>>()
    val loginImages: LiveData<List<Int>> = _loginImages

    init {
        _loginImages.value =
            listOf(
                R.drawable.img_login_1,
                R.drawable.img_login_2,
                R.drawable.img_login_3,
                R.drawable.img_login_4,
            )
    }
}