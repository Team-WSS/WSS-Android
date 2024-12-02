package com.into.websoso.ui.activityDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.activityDetail.ActivityDetailActivity.Companion.SOURCE_MY_ACTIVITY
import com.into.websoso.ui.activityDetail.model.ActivityDetailUiState
import com.into.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.into.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableLiveData<ActivityDetailUiState>()
    val uiState: LiveData<ActivityDetailUiState> get() = _uiState

    private val _likeState = MutableLiveData<ActivityLikeState>()

    private val size: Int = ACTIVITY_LOAD_SIZE

    var source: String?
        get() = savedStateHandle["source"]
        set(value) {
            savedStateHandle["source"] = value
        }

    var userId: Long
        get() = savedStateHandle["userId"] ?: DEFAULT_USER_ID
        set(value) {
            savedStateHandle["userId"] = value
        }

    init {
        _uiState.value = ActivityDetailUiState()
    }

    fun updateRefreshedActivities() {
        _uiState.value = uiState.value?.copy(lastFeedId = 0L)
        updateUserActivities(userId)
    }

    fun updateUserActivities(userId: Long) {
        this.userId = userId
        if (source == SOURCE_MY_ACTIVITY) {
            updateMyActivities()
        } else {
            updateOtherUserActivities(userId)
        }
    }

    private fun updateMyActivities() {
        _uiState.value = uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyActivities(
                    uiState.value?.lastFeedId ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() },
                    lastFeedId = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L,
                    isError = false,
                )
            }.onFailure { exception ->
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    isError = true,
                )
            }
        }
    }

    private fun updateOtherUserActivities(userId: Long) {
        _uiState.value = uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = uiState.value?.lastFeedId ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() },
                    lastFeedId = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L,
                    isError = false,
                )
            }.onFailure { exception ->
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    isError = true,
                )
            }
        }
    }

    fun updateActivityLike(isLiked: Boolean, feedId: Long, currentLikeCount: Int) {
        viewModelScope.launch {
            runCatching {
                if (isLiked) {
                    feedRepository.saveLike(false, feedId)
                } else {
                    feedRepository.saveLike(true, feedId)
                }
            }.onSuccess {
                val newLikeCount = if (isLiked) currentLikeCount - 1 else currentLikeCount + 1
                _likeState.value = ActivityLikeState(feedId, !isLiked, newLikeCount)
                updateLikeStateInUi(feedId, !isLiked, newLikeCount)
            }.onFailure {

            }
        }
    }

    private fun updateLikeStateInUi(feedId: Long, isLiked: Boolean, likeCount: Int) {
        _uiState.value = uiState.value?.copy(
            activities = uiState.value?.activities?.map { activity ->
                if (activity.feedId == feedId) {
                    activity.copy(
                        isLiked = isLiked,
                        likeCount = likeCount,
                    )
                } else {
                    activity
                }
            } ?: emptyList()
        )
    }

    fun updateRemovedFeed(feedId: Long) {
        viewModelScope.launch {
            _uiState.value = uiState.value?.copy(isLoading = true)
            runCatching {
                feedRepository.saveRemovedFeed(feedId)
            }.onSuccess {
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    activities = uiState.value?.activities?.filter { it.feedId != feedId }
                        ?: emptyList(),
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    isError = true,
                )
            }
        }
    }

    fun updateReportedSpoilerFeed(feedId: Long) {
        _uiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _uiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveSpoilerFeed(feedId)
                }.onSuccess {
                    _uiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _uiState.value = feedUiState.copy(
                        isLoading = false,
                        isError = true,
                    )
                }
            }
        }
    }

    fun updateReportedImpertinenceFeed(feedId: Long) {
        _uiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _uiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveImpertinenceFeed(feedId)
                }.onSuccess {
                    _uiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _uiState.value = feedUiState.copy(
                        isLoading = false,
                        isError = true,
                    )
                }
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 100
        const val DEFAULT_USER_ID = -1L
    }
}