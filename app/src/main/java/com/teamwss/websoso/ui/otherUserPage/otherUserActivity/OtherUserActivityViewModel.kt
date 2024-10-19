package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.otherUserPage.otherUserActivity.model.OtherUserActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserActivityViewModel @Inject constructor(
    private val otherUserActivityRepository: UserRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _otherUserActivityUiState: MutableLiveData<OtherUserActivityUiState> =
        MutableLiveData(OtherUserActivityUiState())
    val otherUserActivityUiState: LiveData<OtherUserActivityUiState> get() = _otherUserActivityUiState

    private val _lastFeedId: MutableLiveData<Long> = MutableLiveData(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userId: MutableLiveData<Long> = MutableLiveData()
    val userId: LiveData<Long> get() = _userId

    private val size: Int = ACTIVITY_LOAD_SIZE

    fun updateUserId(userId: Long) {
        _userId.value = userId
        updateOtherUserActivities(userId)
    }

    private fun updateOtherUserActivities(userId: Long) {
        viewModelScope.launch {
            _otherUserActivityUiState.value =
                _otherUserActivityUiState.value?.copy(isLoading = true)
            runCatching {
                otherUserActivityRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = lastFeedId.value ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _otherUserActivityUiState.value = _otherUserActivityUiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() }.take(ACTIVITY_COUNT),
                )
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {
                _otherUserActivityUiState.value = _otherUserActivityUiState.value?.copy(
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
                _otherUserActivityUiState.value = _otherUserActivityUiState.value?.copy(
                    likeState = ActivityLikeState(feedId, !isLiked, newLikeCount),
                )

                saveActivityLikeState(feedId, !isLiked, newLikeCount)
            }.onFailure {
            }
        }
    }

    private fun saveActivityLikeState(feedId: Long, isLiked: Boolean, likeCount: Int) {
        _otherUserActivityUiState.value = _otherUserActivityUiState.value?.copy(
            activities = _otherUserActivityUiState.value?.activities?.map { activity ->
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

    fun updateReportedSpoilerFeed(feedId: Long) {
        otherUserActivityUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _otherUserActivityUiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveSpoilerFeed(feedId)
                }.onSuccess {
                    _otherUserActivityUiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _otherUserActivityUiState.value = feedUiState.copy(
                        isLoading = false,
                        error = true,
                    )
                }
            }
        }
    }

    fun updateReportedImpertinenceFeed(feedId: Long) {
        otherUserActivityUiState.value?.let { feedUiState ->
            viewModelScope.launch {
                _otherUserActivityUiState.value = feedUiState.copy(isLoading = true)
                runCatching {
                    feedRepository.saveImpertinenceFeed(feedId)
                }.onSuccess {
                    _otherUserActivityUiState.value = feedUiState.copy(isLoading = false)
                }.onFailure {
                    _otherUserActivityUiState.value = feedUiState.copy(
                        isLoading = false,
                        error = true,
                    )
                }
            }
        }
    }

    companion object {
        const val ACTIVITY_COUNT = 5
        const val ACTIVITY_LOAD_SIZE = 10
    }
}