package com.teamwss.websoso.ui.main.myPage.myActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val myActivityRepository: UserRepository,
    private val feedRepository: FeedRepository,
    ) : ViewModel() {

    private val _myActivity = MutableLiveData<List<ActivityModel>>()
    val myActivity: LiveData<List<ActivityModel>> get() = _myActivity

    private val _likeState = MutableLiveData<ActivityLikeState>()
    val likeState: LiveData<ActivityLikeState> get() = _likeState

    private val _lastFeedId = MutableLiveData<Long>().apply { value = 0L }
    val lastFeedId: LiveData<Long> get() = _lastFeedId

    private val _userProfile = MutableLiveData<UserProfileModel>()
    val userProfile: LiveData<UserProfileModel> get() = _userProfile

    private var userId: Long = 2L

    private val size: Int = 10

    init {
        updateMyActivities()
    }

    private fun updateMyActivities() {
        viewModelScope.launch {
            runCatching {
                myActivityRepository.fetchUserFeeds(
                    userId,
                    lastFeedId.value ?: 0L,
                    size,
                )
            }.onSuccess { response ->
                _myActivity.value = response.feeds.map { it.toUi() }.take(5)

                _lastFeedId.value = response.feeds.lastOrNull()?.feedId?.toLong() ?: 0L
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
        _myActivity.value = _myActivity.value?.map { activity ->
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
}