package com.teamwss.websoso.ui.feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.domain.usecase.GetCategoryByUserGenderUseCase
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.mapper.FeedMapper.toPresentation
import kotlinx.coroutines.launch

class FeedViewModel(
    private val getFeedsUseCase: GetFeedsUseCase,
    getCategoryByUserGenderUseCase: GetCategoryByUserGenderUseCase,
) : ViewModel() {
    private val _uiState: MutableLiveData<FeedUiState> = MutableLiveData(FeedUiState())
    val uiState: LiveData<FeedUiState> get() = _uiState

    init {
        viewModelScope.launch {
            runCatching {
                getFeedsUseCase()
            }.onSuccess { feeds ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    categories = getCategoryByUserGenderUseCase(),
                    feeds = feeds.map { it.toPresentation() },
                )
            }.onFailure {
                Log.d("123123", it.toString())
                _uiState.value = uiState.value?.copy(
                    loading = false, error = true,
                )
            }
        }
    }

    fun fetchFeedsByCategory(category: Category) {
        viewModelScope.launch {
            runCatching {
                getFeedsUseCase(category.title)
            }.onSuccess { feeds ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    feeds = feeds.map { it.toPresentation() },
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false, error = true,
                )
            }
        }
    }

    fun updateLikeCount(isSelected: Boolean, selectedFeedId: Long) {
        val uiState = uiState.value ?: throw IllegalArgumentException()
        val count = if (isSelected) -1 else 1
        val updatedFeeds = uiState.feeds.map { feed ->
            feed.takeIf { feed.id == selectedFeedId }?.copy(
                likeCount = feed.likeCount + count,
                isLiked = !isSelected
            ) ?: feed
        }
        _uiState.value = uiState.copy(feeds = updatedFeeds)
    }

    fun saveLikeCount() {
        // 좋아요 API
    }

    fun saveBlockedUser(userId: Long) {
        // 유저 차단 API
    }

    fun saveReportedSpoilingFeed(feedId: Long) {
        // 스포일러 신고 API - 소소피드
    }

    fun saveReportedImpertinenceFeed(feedId: Long) {
        // 부적절한 표현 신고 API - 소소피드
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FeedViewModel(
                    getFeedsUseCase = WebsosoApp.getFeedsUseCase(),
                    getCategoryByUserGenderUseCase = WebsosoApp.getCategoryByUserGenderUseCase()
                )
            }
        }
    }
}
