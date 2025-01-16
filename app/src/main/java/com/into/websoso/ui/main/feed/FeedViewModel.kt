package com.into.websoso.ui.main.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.common.ui.model.Gender.FEMALE
import com.into.websoso.core.common.ui.model.Gender.MALE
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.domain.usecase.GetFeedsUseCase
import com.into.websoso.ui.main.feed.model.Category
import com.into.websoso.ui.main.feed.model.CategoryModel
import com.into.websoso.ui.main.feed.model.FeedUiState
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
    private val feedRepository: FeedRepository,
    userRepository: UserRepository,
) : ViewModel() {
    private val _categories: MutableLiveData<List<CategoryModel>> = MutableLiveData()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    private val _feedUiState: MutableLiveData<FeedUiState> = MutableLiveData(FeedUiState())
    val feedUiState: LiveData<FeedUiState> get() = _feedUiState

    init {
        viewModelScope.launch {
            val userGender = userRepository.fetchGender()
            val categories: List<CategoryModel> = when (userGender) {
                MALE.genderCode -> "전체,판타지,현판,무협,드라마,미스터리,라노벨,로맨스,로판,BL,기타"
                FEMALE.genderCode -> "전체,로맨스,로판,BL,판타지,현판,무협,드라마,미스터리,라노벨,기타"
                else -> "전체,판타지,현판,무협,드라마,미스터리,라노벨,로맨스,로판,BL,기타"
            }.split(",")
                .map {
                    val category: Category = Category.from(it)
                    CategoryModel(
                        category = category,
                        isSelected = category == Category.ALL,
                    )
                }

            _categories.value = categories
        }
    }

    fun updateFeeds(isRefreshed: Boolean = false) {
        feedUiState.value?.let { feedUiState ->
            if (!feedUiState.isLoadable) return

            viewModelScope.launch {
                val selectedCategory: Category =
                    categories.value?.find { it.isSelected }?.category
                        ?: throw IllegalStateException()

                runCatching {
                    when (feedUiState.feeds.isNotEmpty()) {
                        true -> getFeedsUseCase(
                            selectedCategory.enTitle,
                            feedUiState.feeds.minOf { it.id }
                        )

                        false -> getFeedsUseCase(selectedCategory.enTitle)
                    }
                }.onSuccess { feeds ->
                    if (feeds.category != selectedCategory.enTitle) throw IllegalStateException()
                    // Return result state of error in domain layer later
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toUi() },
                        isRefreshed = isRefreshed,
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

    fun updateSelectedCategory(category: Category) {
        _categories.value = categories.value?.map { categoryUiState ->
            categoryUiState.copy(isSelected = categoryUiState.category == category)
        }
        updateFeedsByCategory(category)
    }

    private fun updateFeedsByCategory(category: Category) {
        feedUiState.value?.let { feedUiState ->
            _feedUiState.value = feedUiState.copy(loading = true)

            viewModelScope.launch {
                runCatching {
                    getFeedsUseCase(category.enTitle)
                }.onSuccess { feeds ->
                    if (feeds.category != category.enTitle) throw IllegalStateException()
                    // Return result state of error in domain layer later
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toUi() },
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

    fun updateRefreshedFeeds(isRefreshed: Boolean) {
        feedUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                val selectedCategory: Category =
                    categories.value?.find { it.isSelected }?.category
                        ?: throw IllegalStateException()
                delay(300)
                runCatching {
                    getFeedsUseCase(selectedCategory.enTitle)
                }.onSuccess { feeds ->
                    if (feeds.category != selectedCategory.enTitle) throw IllegalStateException()
                    // Return result state of error in domain layer later
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        isLoadable = feeds.isLoadable,
                        feeds = feeds.feeds.map { it.toUi() },
                        isRefreshed = isRefreshed,
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
                        },
                        isRefreshed = false,
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
            viewModelScope.launch {
                _feedUiState.value = feedUiState.copy(loading = true)
                runCatching {
                    feedRepository.saveRemovedFeed(feedId)
                }.onSuccess {
                    _feedUiState.value = feedUiState.copy(
                        loading = false,
                        feeds = feedUiState.feeds.filter { it.id != feedId }
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
}
