package com.teamwss.websoso.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.DefaultFeedRepository
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
    private val feedRepository: DefaultFeedRepository,
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
        feedUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                val selectedCategory: Category =
                    categories.find { it.isSelected }?.category ?: throw IllegalStateException()

                runCatching {
                    getFeedsUseCase(selectedCategory.titleEn)
                }.onSuccess { feeds ->
                    if (feeds.category != selectedCategory.titleEn) throw IllegalStateException()
                    // Return result state of error in domain layer later
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toPresentation() },
                    )
                }.onFailure {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateLike(selectedFeedId: Long, isLiked: Boolean, updatedLikeCount: Int) {
        feedUiState.value?.let { feedUiState ->
            val selectedFeed = feedUiState.feeds.find { feedModel ->
                feedModel.id == selectedFeedId
            } ?: throw IllegalArgumentException()

            if (selectedFeed.isLiked == isLiked) return

            viewModelScope.launch {
                runCatching {
                    feedRepository.saveLike(selectedFeed.isLiked, selectedFeedId)
                }.onSuccess {
                    _feedUiState.value = feedUiState.copy(
                        feeds = feedUiState.feeds.map { feedModel ->
                            when (feedModel.id == selectedFeedId) {
                                true -> feedModel.copy(
                                    isLiked = isLiked,
                                    likeCount = updatedLikeCount,
                                )

                                false -> feedModel
                            }
                        }
                    )
                }.onFailure {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedSpoilerFeed(feedId: Long) {
        feedUiState.value?.let { feedUiState ->
            _feedUiState.value = feedUiState.copy(
                feeds = feedUiState.feeds.filter { it.id != feedId }
            )

            viewModelScope.launch {
                _feedUiState.value = feedUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveSpoilerFeed(feedId)
                }.onSuccess {
                    _feedUiState.value = feedUiState.copy(loading = false)
                }.onFailure {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedImpertinenceFeed(feedId: Long) {
        feedUiState.value?.let { feedUiState ->
            _feedUiState.value = feedUiState.copy(
                feeds = feedUiState.feeds.filter { it.id != feedId }
            )

            viewModelScope.launch {
                _feedUiState.value = feedUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveImpertinenceFeed(feedId)
                }.onSuccess {
                    _feedUiState.value = feedUiState.copy(loading = false)
                }.onFailure {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateRemovedFeed(feedId: Long) {
        feedUiState.value?.let { feedUiState ->
            _feedUiState.value = feedUiState.copy(
                feeds = feedUiState.feeds.filter { it.id != feedId }
            )

            viewModelScope.launch {
                _feedUiState.value = feedUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveRemovedFeed(feedId)
                }.onSuccess {
                    _feedUiState.value = feedUiState.copy(loading = false)
                }.onFailure {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }
    }
}
