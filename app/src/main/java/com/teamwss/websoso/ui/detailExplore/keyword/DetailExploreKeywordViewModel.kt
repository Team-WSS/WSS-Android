package com.teamwss.websoso.ui.detailExplore.keyword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailExploreKeywordViewModel @Inject constructor() : ViewModel() {
    private val _searchWord: MutableLiveData<String> = MutableLiveData()
    val searchWord: MutableLiveData<String> get() = _searchWord

    private val _isSearchCancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchCancelButtonVisibility: LiveData<Boolean> get() = _isSearchCancelButtonVisibility

    fun updateSearchCancelButtonVisibility() {
        _isSearchCancelButtonVisibility.value = _searchWord.value.isNullOrEmpty().not()
    }

    fun updateSearchWordEmpty() {
        _searchWord.value = ""
    }
}