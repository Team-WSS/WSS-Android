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
        _uiState.value = ActivityDetailUiState(isLoading = true)
        updateUserActivities()
    }

    fun updateUserActivities() {
        val currentState = uiState.value ?: ActivityDetailUiState()

        if (!currentState.isLoadable || currentState.isLoading) return

        _uiState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            runCatching {
                if (source == SOURCE_MY_ACTIVITY) {
                    userRepository.fetchMyActivities(
                        lastFeedId = currentState.lastFeedId,
                        size = size
                    )
                } else {
                    userRepository.fetchUserFeeds(
                        userId = userId,
                        lastFeedId = currentState.lastFeedId,
                        size = size
                    )
                }
            }.onSuccess { response ->
                val newActivities = response.feeds.map { it.toUi() }

                val isLoadable = newActivities.isNotEmpty()

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isLoadable = isLoadable,
                    activities = (currentState.activities + newActivities)
                        .distinctBy { it.feedId },
                    lastFeedId = if (isLoadable) newActivities.lastOrNull()?.feedId
                        ?: currentState.lastFeedId else currentState.lastFeedId
                )
            }.onFailure {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isError = true
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
        const val ACTIVITY_LOAD_SIZE = 5
        const val DEFAULT_USER_ID = -1L
    }
}