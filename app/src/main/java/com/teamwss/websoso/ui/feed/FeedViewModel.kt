package com.teamwss.websoso.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.mapper.FeedMapper.toPresentation
import kotlinx.coroutines.launch

class FeedViewModel(
    private val getFeedsUseCase: GetFeedsUseCase,
    fakeUserRepository: FakeUserRepository,
) : ViewModel() {
    val gender: String = fakeUserRepository.gender

    private val _uiState: MutableLiveData<FeedUiState> = MutableLiveData(FeedUiState())
    val uiState: LiveData<FeedUiState> get() = _uiState

    init {
        viewModelScope.launch {
            runCatching {
                getFeedsUseCase()
            }.onSuccess { feeds ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    category = feeds.category,
                    feeds = feeds.feeds.map { it.toPresentation() }
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false, error = true,
                )
            }
        }
    }

    fun updateFeedsByCategory(category: Category) {

    }

    fun updateLikeCount(isSelected: Boolean, selectedFeedId: Int) {
        val uiState = uiState.value ?: throw IllegalArgumentException()
        val count = if (isSelected) -1 else 1
        val updatedFeeds = uiState.feeds.map { feed ->
            feed.takeIf { feed.id == selectedFeedId }?.copy(
                likeCount = (feed.likeCount.toInt() + count).toString(),
                isThumbUpSelected = !isSelected
            ) ?: feed
        }

        _uiState.value = uiState.copy(feeds = updatedFeeds)
    }

    fun putLikeCount() {
        // 좋아요
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


