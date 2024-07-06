package com.teamwss.websoso.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.usecase.GetCategoryByUserGenderUseCase
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.Category.Companion.toWrappedCategories
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.feed.model.FeedUiState.CategoryUiState
import com.teamwss.websoso.ui.mapper.toPresentation
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

    fun updateSelectedCategory(category: Category) {
        uiState.value?.let { uiState ->
            val categoriesUiState: List<CategoryUiState> =
                uiState.categories.map { categoryUiState ->
                    categoryUiState.copy(isSelected = categoryUiState.category == category)
                }

            _uiState.value = uiState.copy(categories = categoriesUiState)
        }
    }

    fun updateFeeds() {
        viewModelScope.launch {
            val selectedCategory: Category = uiState.value?.let { feedUiState ->
                feedUiState.categories.find { it.isSelected }
            }?.category ?: throw IllegalStateException()

            runCatching {
                getFeedsUseCase(selectedCategory.title)
            }.onSuccess { feeds ->
                if (feeds.category != selectedCategory.title) throw IllegalStateException()
                // Return result state of error in domain layer later
                _uiState.value = uiState.value?.let { feedUiState ->
                    feedUiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toPresentation() },
                    )
                }
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
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
