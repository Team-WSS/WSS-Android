package com.teamwss.websoso.ui.main.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.MyActivityUiState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<MyActivityUiState> = MutableLiveData(MyActivityUiState())
    val uiState: LiveData<MyActivityUiState> get() = _uiState

    private val _lastFeedId: MutableLiveData<Long> = MutableLiveData(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userProfile = MutableLiveData<UserProfileModel>()
    val userProfile: LiveData<UserProfileModel> get() = _userProfile

    private val size: Int = ACTIVITY_LOAD_SIZE

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            _uiState.value = uiState.value?.copy(isLoading = true)
            runCatching {
                userRepository.fetchMyActivities(
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    activities = response.feeds.map { it.toUi() }.take(ACTIVITY_LIMIT_COUNT),
                )
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {
                _uiState.value = uiState.value?.copy(
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
                _uiState.value = uiState.value?.copy(
                    likeState = ActivityLikeState(feedId, !isLiked, newLikeCount),
                )

                saveActivityLikeState(feedId, !isLiked, newLikeCount)
            }.onFailure {

            }
        }
    }

    private fun saveActivityLikeState(feedId: Long, isLiked: Boolean, likeCount: Int) {
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
                    activities = uiState.value?.activities?.filter { it.feedId != feedId } ?: emptyList(),
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    isLoading = false,
                    error = true,
                )
            }
        }
    }

    companion object {
        const val ACTIVITY_LOAD_SIZE = 10
        const val ACTIVITY_LIMIT_COUNT = 5
    }
}
