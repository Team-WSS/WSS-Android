package com.teamwss.websoso.ui.main.explore.normalExplore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.data.repository.FakeNovelRepository

class NormalExploreViewModel(
    private val novelRepository: FakeNovelRepository
) : ViewModel() {
    private val _searchContent: MutableLiveData<String> = MutableLiveData()
    val searchContent: MutableLiveData<String> get() = _searchContent

    private val _cancelButtonVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val cancelButtonVisibility: LiveData<Boolean> get() = _cancelButtonVisibility

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                NormalExploreViewModel(
                    novelRepository = WebsosoApp.getNovelRepository()
                )
            }
        }
    }
}