package com.teamwss.websoso.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.FeedUiState

class FeedViewModel(
    private val getFeedsUseCase: GetFeedsUseCase,
    fakeUserRepository: FakeUserRepository,
) : ViewModel() {
    val gender: String = fakeUserRepository.gender

    private val _uiState: MutableLiveData<FeedUiState> = MutableLiveData()
    val uiState: LiveData<FeedUiState> get() = _uiState

    init {
        _uiState.value = FeedUiState(
            feeds = getFeedsUseCase()
        )
    }

    fun updateFeedsByCategory(category: Category) {

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FeedViewModel(
                    getFeedsUseCase = WebsosoApp.getFeedsUseCase(),
                    fakeUserRepository = WebsosoApp.getUserRepository()
                )
            }
        }
    }
}


