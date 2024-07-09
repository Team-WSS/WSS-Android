package com.teamwss.websoso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.R

class LoginViewModel : ViewModel() {
    private val _images = MutableLiveData<List<Int>>()
    val images: LiveData<List<Int>> = _images

    init {
        _images.value =
            listOf(R.drawable.img_login_1, R.drawable.img_login_2, R.drawable.img_login_3)
    }
}