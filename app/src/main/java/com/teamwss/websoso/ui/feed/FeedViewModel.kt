package com.teamwss.websoso.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.domain.model.Feed
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase

class FeedViewModel(
    private val getFeedsUseCase: GetFeedsUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<List<Feed>> = MutableLiveData()
    val uiState: LiveData<List<Feed>> get() = _uiState

    init {
        _uiState.value = getFeedsUseCase()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FeedViewModel(
                    getFeedsUseCase = WebsosoApp.getFeedsUseCase()
                )
            }
        }
    }
}