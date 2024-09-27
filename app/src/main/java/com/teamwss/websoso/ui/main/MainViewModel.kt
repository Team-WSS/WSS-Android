package com.teamwss.websoso.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.ui.main.model.MainUiState

class MainViewModel: ViewModel() {
    private val _mainUiState : MutableLiveData<MainUiState> = MutableLiveData(MainUiState())
    val mainUiState : LiveData<MainUiState> get() = _mainUiState

    fun updateNickname(){
        // TODO nickname 가져오는 서버통신
    }
}