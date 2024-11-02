package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherUserActivityViewModel @Inject constructor(
    private val otherUserActivityRepository: UserRepository,
    private val feedRepository: FeedRepository,
) : ViewModel() {
    private val _otherUserActivity = MutableLiveData<List<ActivityModel>>()
    val otherUserActivity: LiveData<List<ActivityModel>> get() = _otherUserActivity

    private val _likeState = MutableLiveData<ActivityLikeState>()
    val likeState: LiveData<ActivityLikeState> get() = _likeState

    private val _lastFeedId: MutableLiveData<Long> = MutableLiveData(0L)
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userId: MutableLiveData<Long> = MutableLiveData()
    val userId: LiveData<Long> get() = _userId

    private val size: Int = ACTIVITY_LOAD_SIZE

    fun updateUserId(newUserId: Long) {
        if (_userId.value != newUserId) {
            _userId.value = newUserId
            updateOtherUserActivities(newUserId)
        }
    }

    fun updateOtherUserActivities(userId: Long) {
        viewModelScope.launch {
            runCatching {
                otherUserActivityRepository.fetchUserFeeds(
                    userId = userId,
                    lastFeedId = lastFeedId.value ?: 0L,
                    size = size,
                )
            }.onSuccess { response ->
                _otherUserActivity.value = response.feeds.map { it.toUi() }.take(ACTIVITY_COUNT)
                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
            }.onFailure {

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

                saveActivityLikeState(feedId, !isLiked, newLikeCount)
            }.onFailure {
            }
        }
    }

    private fun saveActivityLikeState(feedId: Long, isLiked: Boolean, likeCount: Int) {
        _otherUserActivity.value = _otherUserActivity.value?.map { activity ->
            if (activity.feedId == feedId) {
                activity.copy(
                    isLiked = isLiked,
                    likeCount = likeCount,
                )
            } else {
                activity
            }
        }
    }

    companion object {
        const val ACTIVITY_COUNT = 5
        const val ACTIVITY_LOAD_SIZE = 10
    }
}