package com.teamwss.websoso.ui.activityDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity.Companion.SOURCE_MY_ACTIVITY
import com.teamwss.websoso.ui.activityDetail.model.ActivityDetailUiState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _activityDetailUiState = MutableLiveData<ActivityDetailUiState>()
    val activityDetailUiState: LiveData<ActivityDetailUiState> get() = _activityDetailUiState

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
        _activityDetailUiState.value = ActivityDetailUiState()
    }

    fun updateRefreshedActivities() {
        _activityDetailUiState.value = _activityDetailUiState.value?.copy(lastFeedId = 0L)
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
        _activityDetailUiState.value = _activityDetailUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyActivities(
                    _activityDetailUiState.value?.lastFeedId ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() },
                    lastFeedId = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L,
                    error = false,
                )
            }.onFailure { exception ->
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    private fun updateOtherUserActivities(userId: Long) {
        _activityDetailUiState.value = _activityDetailUiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = _activityDetailUiState.value?.lastFeedId ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() },
                    lastFeedId = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L,
                    error = false,
                )
            }.onFailure { exception ->
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    error = true,
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
        _activityDetailUiState.value = _activityDetailUiState.value?.copy(
            activities = _activityDetailUiState.value?.activities?.map { activity ->
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
            _activityDetailUiState.value = _activityDetailUiState.value?.copy(isLoading = true)
            runCatching {
                feedRepository.saveRemovedFeed(feedId)
            }.onSuccess {
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    activities = _activityDetailUiState.value?.activities?.filter { it.feedId != feedId }
                        ?: emptyList(),
                )
            }.onFailure {
                _activityDetailUiState.value = _activityDetailUiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    fun updateReportedSpoilerFeed(feedId: Long) {
        activityDetailUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _activityDetailUiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveSpoilerFeed(feedId)
                }.onSuccess {
                    _activityDetailUiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _activityDetailUiState.value = feedUiState.copy(
                        isLoading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedImpertinenceFeed(feedId: Long) {
        activityDetailUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _activityDetailUiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveImpertinenceFeed(feedId)
                }.onSuccess {
                    _activityDetailUiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _activityDetailUiState.value = feedUiState.copy(
                        isLoading = false,
                        error = true,
                    )
                }
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 10
        const val DEFAULT_USER_ID = -1L
    }
}