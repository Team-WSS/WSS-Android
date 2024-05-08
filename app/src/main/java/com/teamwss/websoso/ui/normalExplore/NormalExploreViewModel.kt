package com.teamwss.websoso.ui.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NormalExploreViewModel : ViewModel() {
    private val _searchContent: MutableLiveData<String> = MutableLiveData()
    val searchContent: MutableLiveData<String> get() = _searchContent

    private val _cancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val cancelButtonVisibility: LiveData<Boolean> get() = _cancelButtonVisibility
}