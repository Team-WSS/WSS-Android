package com.teamwss.websoso.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FakeUserRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import com.teamwss.websoso.ui.feed.model.Category
import com.teamwss.websoso.ui.feed.model.CategoryModel
import com.teamwss.websoso.ui.feed.model.FeedUiState
import com.teamwss.websoso.ui.mapper.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
    fakeUserRepository: FakeUserRepository,
) : ViewModel() {
    private val _categories: MutableList<CategoryModel> = mutableListOf()
    val categories: List<CategoryModel> get() = _categories.toList()

    private val _feedUiState: MutableLiveData<FeedUiState> = MutableLiveData(FeedUiState())
    val feedUiState: LiveData<FeedUiState> get() = _feedUiState

    init {
        val categories: List<CategoryModel> = when (fakeUserRepository.gender) {
            "MALE" -> "전체,판타지,현판,무협,드라마,미스터리,라노벨,로맨스,로판,BL,기타"
            "FEMALE" -> "전체,로맨스,로판,BL,판타지,현판,무협,드라마,미스터리,라노벨,기타"
            else -> throw IllegalArgumentException()
        }.split(",")
            .map {
                val category: Category = Category.from(it)
                CategoryModel(
                    category = category,
                    isSelected = category == Category.ALL,
                )
            }

        _categories.addAll(categories)
    }

    fun updateSelectedCategory(category: Category) {
        categories.forEachIndexed { index, categoryUiState ->
            _categories[index] = when {
                categoryUiState.isSelected -> categoryUiState.copy(isSelected = false)
                categoryUiState.category == category -> categoryUiState.copy(isSelected = true)
                else -> return@forEachIndexed
            }
        }
        updateFeeds()
    }

    fun updateFeeds() {
        viewModelScope.launch {
            val selectedCategory: Category =
                categories.find { it.isSelected }?.category ?: throw IllegalStateException()

            runCatching {
                getFeedsUseCase(selectedCategory.titleEn)
            }.onSuccess { feeds ->
                if (feeds.category != selectedCategory.titleEn) throw IllegalStateException()
                // Return result state of error in domain layer later
                feedUiState.value?.let { uiState ->
                    _feedUiState.value = uiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toPresentation() },
                    )
                }
            }.onFailure {
                _feedUiState.value = feedUiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun updateLikeCount(isSelected: Boolean, selectedFeedId: Long) {
        val uiState = feedUiState.value ?: throw IllegalArgumentException()
        val count = if (isSelected) -1 else 1
        val updatedFeeds = uiState.feeds.map { feed ->
            feed.takeIf { feed.id == selectedFeedId }?.copy(
                likeCount = feed.likeCount + count,
                isLiked = !isSelected
            ) ?: feed
        }
        _feedUiState.value = uiState.copy(feeds = updatedFeeds)
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
