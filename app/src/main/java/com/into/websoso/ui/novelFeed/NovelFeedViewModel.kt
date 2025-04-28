package com.into.websoso.ui.novelFeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.model.NovelFeedsEntity
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.novelFeed.model.NovelFeedUiState
import com.into.websoso.ui.novelInfo.NovelInfoViewModel.Companion.UPDATE_TASK_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NovelFeedViewModel
    @Inject
    constructor(
        private val novelRepository: NovelRepository,
        private val feedRepository: FeedRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private var userId: Long = -1
        private val _feedUiState: MutableLiveData<NovelFeedUiState> =
            MutableLiveData(NovelFeedUiState())
        val feedUiState: LiveData<NovelFeedUiState> get() = _feedUiState
        private val _isRefreshed: MutableLiveData<Boolean> = MutableLiveData(false)
        val isRefreshed: LiveData<Boolean> get() = _isRefreshed
        private val _isLogin: MutableLiveData<Boolean> = MutableLiveData(false)
        val isLogin: LiveData<Boolean> get() = _isLogin

        fun updateFeeds(
            novelId: Long,
            isAgainRefresh: Boolean = false,
        ) {
            feedUiState.value?.let { feedUiState ->
                if (!feedUiState.isLoadable && !isAgainRefresh) return

                viewModelScope.launch {
                    runCatching {
                        listOf(
                            async {
                                novelRepository.fetchNovelFeeds(
                                    novelId = novelId,
                                    lastFeedId = when (feedUiState.feeds.isNotEmpty()) {
                                        true -> feedUiState.feeds.minOf { it.id }
                                        false -> 0
                                    },
                                    size = 20,
                                )
                            },
                            async { userRepository.fetchUserId() },
                        ).awaitAll()
                    }.onSuccess { responses ->
                        val feeds = responses[0] as NovelFeedsEntity
                        userId = responses[1] as Long

                        _feedUiState.value = feedUiState.copy(
                            feeds = feedUiState.feeds + feeds.feeds.map { it.toUi() },
                            error = false,
                            loading = false,
                            isLoadable = feeds.isLoadable,
                        )

                        _isRefreshed.value = isAgainRefresh
                    }.onFailure {
                        _feedUiState.value = feedUiState.copy(
                            error = true,
                            loading = false,
                            isLoadable = false,
                        )
                    }
                }
            }
        }

        fun updateFeedWithDelay(
            novelId: Long,
            isAgainRefresh: Boolean,
        ) {
            viewModelScope.launch {
                delay(UPDATE_TASK_DELAY)
                updateFeeds(novelId, isAgainRefresh)
            }
        }

        fun updateIsRefreshed(boolean: Boolean) {
            _isRefreshed.value = boolean
        }

        fun updateRefreshedFeeds(novelId: Long) {
            feedUiState.value?.let { feedUiState ->
                viewModelScope.launch {
                    runCatching {
                        novelRepository.fetchNovelFeeds(
                            novelId = novelId,
                            lastFeedId = 0,
                            size = 20,
                        )
                    }.onSuccess { feeds ->
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

        fun updateLike(
            selectedFeedId: Long,
            isLiked: Boolean,
            updatedLikeCount: Int,
        ) {
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

        fun updateRemovedFeed(feedId: Long) {
            feedUiState.value?.let { feedUiState ->
                viewModelScope.launch {
                    _feedUiState.value = feedUiState.copy(loading = true)
                    runCatching {
                        feedRepository.saveRemovedFeed(feedId)
                    }.onSuccess {
                        _feedUiState.value = feedUiState.copy(
                            loading = false,
                            feeds = feedUiState.feeds.filter { it.id != feedId },
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
                        _feedUiState.value = feedUiState.copy(
                            loading = false,
                            feeds = feedUiState.feeds.filter { it.id != feedId },
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

        fun updateReportedImpertinenceFeed(feedId: Long) {
            feedUiState.value?.let { feedUiState ->
                viewModelScope.launch {
                    _feedUiState.value = feedUiState.copy(loading = true)
                    runCatching {
                        feedRepository.saveImpertinenceFeed(feedId)
                    }.onSuccess {
                        _feedUiState.value = feedUiState.copy(
                            loading = false,
                            feeds = feedUiState.feeds.filter { it.id != feedId },
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

        fun updateLoginStatus() {
            viewModelScope.launch {
                runCatching {
                    userRepository.fetchIsLogin()
                }.onSuccess { isLogin ->
                    // TODO: _isLogin.value = isLogin
                    _isLogin.value = true
                }.onFailure {
                    throw it
                }
            }
        }
    }
