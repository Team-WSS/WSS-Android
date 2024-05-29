package com.teamwss.websoso.ui.feed

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
import com.teamwss.websoso.ui.feed.model.Category.Companion.toWrappedCategories
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.mapper.FeedMapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
    getCategoryByUserGenderUseCase: GetCategoryByUserGenderUseCase,
) : ViewModel() {
    val category: List<Category> = getCategoryByUserGenderUseCase().toWrappedCategories()

    private val _uiState: MutableLiveData<FeedUiState> =
        MutableLiveData(FeedUiState(categories = category.toPresentation()))
    val uiState: LiveData<FeedUiState> get() = _uiState

    init {
        updateFeedsByCategory()
    }

    fun updateFeedsByCategory(category: Category) {
        // non-nullable 수정
        _uiState.value = uiState.value?.copy(
            categories = uiState.value?.categories?.map { categoryUiState ->
                categoryUiState.takeIf { categoryUiState.category == category }?.copy(
                    isSelected = true,
                ) ?: categoryUiState.copy(isSelected = false)
            } ?: uiState.value!!.categories
        )
        updateFeedsByCategory()
    }

    fun updateFeedsByCategory() {
        val category: Category = uiState.value!!.categories.find { it.isSelected }!!.category
        // response category 대조 로직 추가 예정
        viewModelScope.launch {
            runCatching {
                getFeedsUseCase(category = category.title)
            }.onSuccess { feeds ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    isLoadable = feeds.isLoadable,
                    feeds = feeds.feeds.map { it.toPresentation() },
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
}
